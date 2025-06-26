package tw.amer.cia.core.service.host;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import tw.amer.cia.core.common.DataModifyAction;
import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.common.gateway.GatewayFormatter;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.component.functional.coriander.CallClientApiComponent;
import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.*;
import tw.amer.cia.core.model.database.dao.*;
import tw.amer.cia.core.model.pojo.service.common.apikey.*;
import tw.amer.cia.core.model.pojo.service.common.gateway.ExternalGatewayInfoDto;
import tw.amer.cia.core.model.pojo.service.host.control.ListApikeyPermissionDto;
import tw.amer.cia.core.model.pojo.service.host.web.apikeyManagement.ApiIdAndFabIdProjection;
import tw.amer.cia.core.model.pojo.service.host.web.apikeyManagement.Web_ApiEndpointDataDetailDto;
import tw.amer.cia.core.model.pojo.service.host.web.apikeyManagement.Web_ApiGwEndpointDataDto;
import tw.amer.cia.core.model.pojo.service.host.web.apikeyManagement.Web_ModifyApikeyPermissionItemOutDto;
import tw.amer.cia.core.service.core.ValidateService;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@HostService
@Slf4j
public class ApikeyServiceForHost {

    @Autowired
    ValidateService validateService;
    @Autowired
    GwApikeyEntityRepo gwApikeyEntityRepo;
    @Autowired
    GwApikeyPermissionRepo gwApikeyPermissionRepo;
    @Autowired
    RoleAuthorityEntityRepo roleAuthorityEntityRepo;
    @Autowired
    ApiEndpointEntityRepo apiEndpointEntityRepo;
    @Autowired
    ApiDpyEntityRepo apiDpyEntityRepo;
    @Autowired
    CallClientApiComponent callClientApiComponent;
    @Autowired
    GatewayInfoServiceForHost gatewayInfoServiceForHost;

    @Transactional(rollbackFor = {Exception.class})
    public String createApikeyFromHost(CreateApikeyHostDto dto) throws DataSourceAccessException, CiaProcessorException {

        // Data Check
        boolean illegalInput = StringUtils.isEmpty(dto.getKeyName()) ||
                StringUtils.isEmpty(dto.getRoleId());
        if (illegalInput) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_KEY_CREATE_KEY_INCORRECT_INPUT.getCompleteMessage());
        }
        validateService.validateApikeyNameDuplicate(dto.getRoleId(), dto.getKeyName());

        // Verify Role Authorities
        boolean illegalAuthoritiesList = validateService.illegalRoleAuthoritiesByApiId(dto.getRoleId(), dto.getFabId(), dto.getApiIdList());
        if (illegalAuthoritiesList) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_AUTHORITY_INSUFFICIENT_ROLE_AUTHORITY.getCompleteMessage());
        }


        // Action
        GwApikeyEntity newApikey = GwApikeyEntity.builder()
                .apikeyId(RandomStringUtils.random(
                        GeneralSetting.APIKEY_ID_LENGTH,
                        GeneralSetting.APIKEY_ID_CONTAIN_CHAR,
                        GeneralSetting.APIKEY_ID_CONTAIN_NUMBER))
                .keyName(dto.getKeyName())
                .keyDesc(StringUtils.defaultString(dto.getKeyDesc(), ""))
                .roleId(dto.getRoleId())
                .createTime(Instant.now())
                .isActive(GeneralSetting.GENERAL_POSITIVE_STRING)
                .build();
        log.debug("Created Apikey: {}", newApikey.toString());

        // Apikey資料儲存
        gwApikeyEntityRepo.save(newApikey);

        // 若有進行 FAB 段部署
        if (CollectionUtils.isNotEmpty(dto.getApiIdList())) {

            List<GwApikeyPermissionEntity> newPermissionList = new ArrayList<>();
            for (String onGrantApiId : dto.getApiIdList()) {
                GwApikeyPermissionEntity rKeyPermission = GwApikeyPermissionEntity.builder()
                        .apikeyId(newApikey.getApikeyId())
                        .fabId(dto.getFabId())
                        .apiId(onGrantApiId)
                        .createTime(Instant.now())
                        .build();
                newPermissionList.add(rKeyPermission);
            }

            // 先進行資料儲存
            gwApikeyPermissionRepo.saveAll(newPermissionList);

            // 針對對應Client下達指令
            callClientApiComponent.createOrUpdateApikeyFromHost(dto.getFabId(), newApikey);
            callClientApiComponent.manageApikeyPermissionFromHostBatchThroughFabId(dto.getFabId(), null, newPermissionList);
        }

        return newApikey.getApikeyId();
    }

    @Transactional(rollbackFor = {Exception.class})
    public void updateApikeyFromHost(UpdateApikeyHostDto dto) throws DataSourceAccessException, CiaProcessorException {

        // Data Check
        boolean illegalInput = StringUtils.isEmpty(dto.getApikeyId()) ||
                StringUtils.isEmpty(dto.getRoleId()) ||
                StringUtils.isEmpty(dto.getFabId());
        if (illegalInput) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_KEY_UPDATE_KEY_INCORRECT_INPUT.getCompleteMessage());
        }

        // Verify Role Authorities
        boolean illegalAuthoritiesList = validateService.illegalRoleAuthoritiesByApiId(dto.getRoleId(), dto.getFabId(), dto.getApiIdList());
        if (illegalAuthoritiesList) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_AUTHORITY_INSUFFICIENT_ROLE_AUTHORITY.getCompleteMessage());
        }

        // Action
        GwApikeyEntity onModifyApikey = validateService.validateApikeyByApikeyId(dto.getApikeyId());

        List<GwApikeyPermissionEntity> oldPermissionList = gwApikeyPermissionRepo.findByFabIdAndApikeyId(dto.getFabId(), onModifyApikey.getApikeyId());
        List<GwApikeyPermissionEntity> newPermissionList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(dto.getApiIdList())) {
            for (String onGrantApiId : dto.getApiIdList()) {
                GwApikeyPermissionEntity cKeyPermission;
                Optional<GwApikeyPermissionEntity> inSearchKeyPermission = gwApikeyPermissionRepo.findByFabIdAndApikeyIdAndApiId(dto.getFabId(), onModifyApikey.getApikeyId(), onGrantApiId);
                if (inSearchKeyPermission.isPresent()) {
                    cKeyPermission = inSearchKeyPermission.get();
                } else {
                    cKeyPermission = GwApikeyPermissionEntity.builder()
                            .apikeyId(onModifyApikey.getApikeyId())
                            .fabId(dto.getFabId())
                            .apiId(onGrantApiId)
                            .createTime(Instant.now())
                            .build();
                }
                newPermissionList.add(cKeyPermission);
            }
        }
        // 捕捉差集
        // 1.  愈刪除權限之微服務清單；即原本有，更新後將消失的部分。
        List<GwApikeyPermissionEntity> waitRevokeList = (List<GwApikeyPermissionEntity>) CollectionUtils.subtract(oldPermissionList, newPermissionList);
        // 2.  愈新建權限之微服務清單；即原本沒有，更新後將加入的部分。
        List<GwApikeyPermissionEntity> waitGrantList = (List<GwApikeyPermissionEntity>) CollectionUtils.subtract(newPermissionList, oldPermissionList);


        // Do Process
        callClientApiComponent.manageApikeyPermissionFromHostBatchThroughFabId(dto.getFabId(), waitRevokeList, waitGrantList);

        // Store Data in DB
        gwApikeyPermissionRepo.deleteByFabIdAndApikeyIdAndApiIdIn(dto.getFabId(), onModifyApikey.getApikeyId(),
                waitRevokeList.stream()
                        .map(GwApikeyPermissionEntity::getApiId)
                        .collect(Collectors.toList()));
        gwApikeyPermissionRepo.saveAll(waitGrantList);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteApikeyFromHost(DeleteApikeyHostDto dto) throws DataSourceAccessException, CiaProcessorException {
        // Data Check
        if (StringUtils.isEmpty(dto.getRoleId()) ||
                StringUtils.isEmpty(dto.getApikeyId())) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_KEY_DELETE_KEY_INCORRECT_INPUT.getCompleteMessage());
        }

        GwApikeyEntity waitDeleteApikey = validateService.validateApikeyByApikeyIdAndRoleId(dto.getApikeyId(), dto.getRoleId());

        // Action 1 purge permissions
        List<GwApikeyPermissionEntity> keyPermissionList;
        keyPermissionList = gwApikeyPermissionRepo.findByApikeyId(dto.getApikeyId());

        if (CollectionUtils.isNotEmpty(keyPermissionList)) {
            // 1. purge permissions to clients
            Set<String> fabIdSet = keyPermissionList.stream().map(GwApikeyPermissionEntity::getFabId).collect(Collectors.toSet());
            for (String fabId : fabIdSet) {
                List<GwApikeyPermissionEntity> permissionListByFabId = keyPermissionList.stream()
                        .filter(obj -> fabIdSet.contains(obj.getFabId()))
                        .collect(Collectors.toList());
                // 清洗 Apikey權限的同時，一併清除FAB中的Apikey
                callClientApiComponent.manageApikeyPermissionFromHostBatchThroughFabId(fabId, permissionListByFabId, null);
            }

            // 2. purge permissions in DB
            gwApikeyPermissionRepo.deleteAll(keyPermissionList);
        }

        // Action 2 purge Apikey
        // Client Apikey 會由Client 進行權現刪除的時候自檢刪除
        gwApikeyEntityRepo.delete(waitDeleteApikey);
    }

    public void updateApikeyPermissionByApikeyIdAndScopeList(String roleId, String apikeyId, List<String> enableFabIdList, List<String> disableFabIdList) throws DataSourceAccessException, CiaProcessorException {
        validateService.validateApikeyByApikeyId(apikeyId);
        if (CollectionUtils.isNotEmpty(enableFabIdList)) {
            for (String fabId : enableFabIdList) {
                this.updateApikeyFromHost(
                        UpdateApikeyHostDto.builder()
                                .fabId(fabId)
                                .roleId(roleId)
                                .apikeyId(apikeyId)
                                .apiIdList(
                                        roleAuthorityEntityRepo.findApiIdByRoleIdAndFabId(roleId, fabId)
                                )
                                .build());
            }
        }
        if (CollectionUtils.isNotEmpty(disableFabIdList)) {
            for (String fabId : disableFabIdList) {
                this.updateApikeyFromHost(
                        UpdateApikeyHostDto.builder()
                                .fabId(fabId)
                                .roleId(roleId)
                                .apikeyId(apikeyId)
                                .apiIdList(Collections.emptyList())
                                .build());
            }
        }
    }

    public List<ListApikeyPermissionDto> retrieveApikeyInfoByFabAndApikeyId(String fabId, String apikeyId) {
        List<ListApikeyPermissionDto> dtoList = gwApikeyPermissionRepo.apiListApikeyPermissionByFabIdAndApikeyId(fabId, apikeyId);
        return dtoList;
    }

    @Transactional(rollbackFor = {Exception.class})
    public void revokeApikeyPermissionsByFab(String fabId, List<GwApikeyPermissionEntity> onRevokePermissions) throws CiaProcessorException {
        callClientApiComponent.manageApikeyPermissionFromHostBatchThroughFabId(fabId, onRevokePermissions, null);
        gwApikeyPermissionRepo.deleteAll(onRevokePermissions);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void revokeApikeyPermissionWhenApiUndeployByFab(String fabId, String apiId) throws CiaProcessorException {
        List<GwApikeyPermissionEntity> relatedKeyPermission = gwApikeyPermissionRepo.findByFabIdAndApiId(fabId, apiId);
        this.revokeApikeyPermissionsByFab(fabId, relatedKeyPermission);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void revokeApikeyPermissionByFabAndApiIds(String fabId, String apikeyId, List<String> apiIdList) throws CiaProcessorException {
        List<GwApikeyPermissionEntity> relatedKeyPermission = gwApikeyPermissionRepo.findByFabIdAndApikeyIdAndApiIds(
                fabId, apikeyId, apiIdList);
        this.revokeApikeyPermissionsByFab(fabId, relatedKeyPermission);
    }

    public void deleteApikeyByRole(String roleId) throws DataSourceAccessException, CiaProcessorException {
        // Data Check
        if (StringUtils.isEmpty(roleId)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_KEY_DELETE_KEY_BY_ROLE_INCORRECT_INPUT.getCompleteMessage());
        }
        validateService.validateRoleId(roleId);

        // eraser apikey permissions
        List<GwApikeyEntity> roleApikeyList = gwApikeyEntityRepo.findByRoleId(roleId);

        for (GwApikeyEntity apikey : roleApikeyList) {
            DeleteApikeyHostDto deleteApikeyDto = DeleteApikeyHostDto.builder()
                    .roleId(roleId)
                    .apikeyId(apikey.getApikeyId())
                    .build();
            this.deleteApikeyFromHost(deleteApikeyDto);
        }
    }

    public void updateApikeyStatus(String apikeyId) throws DataSourceAccessException, CiaProcessorException {
        GwApikeyEntity onCheckApikey = validateService.validateApikeyByApikeyId(apikeyId);

        onCheckApikey.setIsActive(StringUtils.equalsIgnoreCase(onCheckApikey.getIsActive(), DataModifyAction.DATA_IS_ACTIVE_TRUE) ?
                DataModifyAction.DATA_IS_ACTIVE_FALSE : DataModifyAction.DATA_IS_ACTIVE_TRUE);
        gwApikeyEntityRepo.save(onCheckApikey);
        callClientApiComponent.tryUpdateApikeyActiveStatusBroadcastNoReply(apikeyId);

    }

    public void deleteApikeyCheckFromClient(String apikeyId) throws DataSourceAccessException {
        GwApikeyEntity onCheckApikey = validateService.validateApikeyByApikeyId(apikeyId);
        if (CollectionUtils.isEmpty(gwApikeyPermissionRepo.findByApikeyId(apikeyId))) {
            gwApikeyEntityRepo.delete(onCheckApikey);
        }
    }

    public List<CompleteApikeyDto> retrieveCompleteApikeyDataByFabId(Collection<String> fabIdSet) {
        // Build Result
        List<CompleteApikeyDto> result = new ArrayList<>();

        // Process
        List<GwApikeyPermissionEntity> permissionList = gwApikeyPermissionRepo.findByFabIdIn(fabIdSet);
        if (CollectionUtils.isNotEmpty(permissionList)) {
            Set<String> apikeyIdSet = permissionList.stream().map(GwApikeyPermissionEntity::getApikeyId).collect(Collectors.toSet());
            apikeyIdSet.forEach(apikeyId ->
            {
                Optional<GwApikeyEntity> onSearchApikey = gwApikeyEntityRepo.findByApikeyId(apikeyId);
                if (onSearchApikey.isPresent()) {
                    GwApikeyEntity targetApikey = onSearchApikey.get();
                    List<GwApikeyPermissionEntity> targetPermissionList = permissionList.stream()
                            .filter(obj -> StringUtils.isNotEmpty(obj.getApikeyId()) &&
                                    StringUtils.equals(obj.getApikeyId(), apikeyId))
                            .collect(Collectors.toList());
                    result.add(CompleteApikeyDto.builder()
                            .apikey(targetApikey)
                            .permissionList(targetPermissionList)
                            .build());
                }
            });
        }
        return result;
    }

    public void createOrUpdateApiKeyFromClient(String identifier, GwApikeyEntity inDto) throws CiaProcessorException {
        Optional<GwApikeyEntity> inSearchKey = gwApikeyEntityRepo.findByApikeyId(inDto.getApikeyId());
        GwApikeyEntity onSaveKey;
        if (inSearchKey.isPresent()) {
            onSaveKey = inSearchKey.get();
            BeanUtils.copyNonNullProperties(inDto, onSaveKey);
            gwApikeyEntityRepo.save(inDto);

            List<GwApikeyPermissionEntity> permissionList = gwApikeyPermissionRepo.findByApikeyId(inDto.getApikeyId());
            Set<String> updateFabSet = permissionList.stream().map(GwApikeyPermissionEntity::getFabId).collect(Collectors.toSet());
            for (String fabId : updateFabSet) {
                callClientApiComponent.createOrUpdateApikeyFromHostExclude(fabId, identifier, inDto);
            }
        } else {
            gwApikeyEntityRepo.save(inDto);
        }
    }

    public void updateApikeyPermissionBatchFromClient(String identifier, String apikeyId, UpdateApikeyPermissionFromClientCompleteDto permissionDto) throws DataSourceAccessException, CiaProcessorException {
        GwApikeyEntity onUpdateKey = validateService.validateApikeyByApikeyId(apikeyId);

        if (CollectionUtils.isNotEmpty(permissionDto.getRevokePermissionList())) {
            for (GwApikeyPermissionEntity cKeyPermission : permissionDto.getRevokePermissionList()) {
                gwApikeyPermissionRepo.deleteByFabIdAndApikeyIdAndApiId(
                        cKeyPermission.getFabId(),
                        cKeyPermission.getApikeyId(),
                        cKeyPermission.getApiId()
                );
            }
        }
        if (CollectionUtils.isNotEmpty(permissionDto.getGrantPermissionList())) {
            gwApikeyPermissionRepo.saveAll(permissionDto.getGrantPermissionList());
        }

        Set<String> effectedFabSet = Stream.concat(
                Optional.ofNullable(permissionDto.getRevokePermissionList()).orElseGet(Collections::emptyList).stream(),
                Optional.ofNullable(permissionDto.getGrantPermissionList()).orElseGet(Collections::emptyList).stream()
        ).map(GwApikeyPermissionEntity::getFabId).collect(Collectors.toSet());

        for (String fabId : effectedFabSet) {
            callClientApiComponent.manageApikeyPermissionFromHostBatchThroughFabListExclude(fabId, identifier,
                    Optional.ofNullable(permissionDto.getRevokePermissionList()).orElseGet(Collections::emptyList).stream()
                            .filter(permission -> fabId.equals(permission.getFabId()))
                            .collect(Collectors.toList()),
                    Optional.ofNullable(permissionDto.getGrantPermissionList()).orElseGet(Collections::emptyList).stream()
                            .filter(permission -> fabId.equals(permission.getFabId()))
                            .collect(Collectors.toList()));
        }
    }

    public String duplicateApikeyWithKeyName(String roleId, String sourceApikeyId, String newApikeyName) throws DataSourceAccessException, CiaProcessorException {
        Map<String, List<String>> fabIdApiListMap = new HashMap<>();

        List<GwApikeyPermissionEntity> sourceKeyPermissions = gwApikeyPermissionRepo.findByApikeyId(sourceApikeyId);
        if (CollectionUtils.isNotEmpty(sourceKeyPermissions)) {
            Set<String> fabIdSet = sourceKeyPermissions.stream().map(GwApikeyPermissionEntity::getFabId).collect(Collectors.toSet());
            fabIdSet.forEach(fabId -> {
                List<String> apiList = sourceKeyPermissions.stream()
                        .filter(cKeyPermission -> StringUtils.equalsIgnoreCase(fabId, cKeyPermission.getFabId()))
                        .map(GwApikeyPermissionEntity::getApiId)
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(apiList)) {
                    fabIdApiListMap.put(fabId, apiList);
                }
            });
            String newApikeyId = this.createApikeyFromHostAcrossFab(
                    CreateApikeyAcrossFabHostDto.builder()
                            .roleId(roleId)
                            .keyName(newApikeyName)
                            .fabIdApiListMap(fabIdApiListMap).build()
            );
            if (StringUtils.isNotEmpty(newApikeyId)) {
                return newApikeyId;
            }
        }
        return Strings.EMPTY;
    }

    public String createApikeyFromHostAcrossFab(CreateApikeyAcrossFabHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        Map<String, List<String>> fabIdApiListMap = inDto.getFabIdApiListMap();

        if (MapUtils.isNotEmpty(fabIdApiListMap)) {
            // 因原設計僅能一次對單一Fab 進行 Apikey鑄造，故沿用舊方法先進行密鑰打造

            String initialFabId = fabIdApiListMap.keySet().stream().findFirst().get();
            if (CollectionUtils.isNotEmpty(fabIdApiListMap.get(initialFabId))) {
                String newApikeyId = this.createApikeyFromHost(
                        CreateApikeyHostDto.builder()
                                .roleId(inDto.getRoleId())
                                .fabId(initialFabId)
                                .keyName(inDto.getKeyName())
                                .keyDesc(inDto.getKeyDesc())
                                .apiIdList(fabIdApiListMap.get(initialFabId))
                                .build()
                );

                // 密鑰打造後，在針對其餘做Fab進行個別更新
                for (String fabId : fabIdApiListMap.keySet()) {
                    boolean isNotInitialFabId = !StringUtils.equalsIgnoreCase(fabId, initialFabId);

                    if (isNotInitialFabId) {
                        List<String> apiList = fabIdApiListMap.get(fabId);
                        if (CollectionUtils.isNotEmpty(apiList)) {
                            this.updateApikeyFromHost(
                                    UpdateApikeyHostDto.builder()
                                            .fabId(fabId)
                                            .roleId(inDto.getRoleId())
                                            .apikeyId(newApikeyId)
                                            .apiIdList(apiList)
                                            .build()
                            );
                        }
                    }
                }
                return newApikeyId;
            }
        }
        return Strings.EMPTY;
    }

    public void updateApikeyPermissionFromHostAcrossFab(UpdateApikeyAcrossFabHostDto inDto) throws DataSourceAccessException, CiaProcessorException {
        Map<String, List<String>> onUpdateDataMap = inDto.getFabIdApiListMap();

        // 若是前端沒有輸入的 FAB ，即等於取消 Apikey 對於該 Fab 的微服務權限
        Set<String> fabIdList = new HashSet<>(roleAuthorityEntityRepo.findFabIdListByRoleId(inDto.getRoleId()));
        fabIdList.forEach(
                fabId -> {
                    boolean notContainThisFab = !onUpdateDataMap.containsKey(fabId);
                    if (notContainThisFab) {
                        // 給予空值，如此一來就會清理掉該 FAB 的 Apikey 許可清單
                        onUpdateDataMap.put(fabId, new ArrayList<>());
                    }
                }
        );

        for (String fabId : onUpdateDataMap.keySet()) {
            List<String> apiList = onUpdateDataMap.get(fabId);
            if (apiList != null) {
                this.updateApikeyFromHost(
                        UpdateApikeyHostDto.builder()
                                .fabId(fabId)
                                .roleId(inDto.getRoleId())
                                .apikeyId(inDto.getApikeyId())
                                .apiIdList(apiList)
                                .build()
                );
            }
        }
    }

    public Map<String, List<Web_ModifyApikeyPermissionItemOutDto>> retrieveApikeyPermissionByApikeyId(String apikeyId) throws DataSourceAccessException {
        GwApikeyEntity retrievedKey = validateService.validateApikeyByApikeyId(apikeyId);
        List<GwApikeyPermissionEntity> permissionList = gwApikeyPermissionRepo.findByApikeyId(apikeyId);
        List<SimplePermissionWithIdDto> permissionWithIdList = gwApikeyPermissionRepo.findPermittedSimplePermissionWithIdDtoByApikeyId(apikeyId);

        Map<String, SimplePermissionWithIdDto> apiIdToSimplePermissionMap = new HashMap<>();
        Map<String, List<Web_ModifyApikeyPermissionItemOutDto>> systemNameToIteapiMap = new HashMap<>();

        // For Computing
        Set<String> permittedApiIdSet = permissionList.stream().map(GwApikeyPermissionEntity::getApiId).collect(Collectors.toSet());

        // Selected Scopes
        Map<String, List<String>> keyPermittedFabIdByApiIdMap = new HashMap<>();
        for (GwApikeyPermissionEntity permission : permissionList) {
            keyPermittedFabIdByApiIdMap.computeIfAbsent(
                    permission.getApiId(), k -> new ArrayList<>()
            ).add(permission.getFabId());
        }
        // 4.2.0 Inject Virtual Site & Sandbox Fab into apikey function listing
        Map<String, List<String>> keyPermittedFabIdByApiIdMapWithSandbox = new HashMap<>();
        for (GwApikeyPermissionEntity permission : permissionList) {
            keyPermittedFabIdByApiIdMapWithSandbox.computeIfAbsent(
                    permission.getApiId(), k -> new ArrayList<>()
            ).add(permission.getFabId());
        }
        Set<ApiIdAndFabIdProjection> apiIdExistsSandBoxFabSet = new HashSet<>(apiDpyEntityRepo.findByFabIdAndApiIdIn(
                GeneralSetting.SANDBOX_FAB.getFabId(), keyPermittedFabIdByApiIdMapWithSandbox.keySet(), ApiIdAndFabIdProjection.class));
        if(CollectionUtils.isNotEmpty(apiIdExistsSandBoxFabSet)){
            apiIdExistsSandBoxFabSet.stream()
                    .filter(p -> keyPermittedFabIdByApiIdMapWithSandbox.containsKey(p.getApiId()))
                    .forEach(p -> keyPermittedFabIdByApiIdMapWithSandbox.get(p.getApiId()).add(p.getFabId()));
        }

        // Total Role Authed Scopes
        List<RoleAuthorityEntity> roleAuthedList = roleAuthorityEntityRepo.findByRoleIdAndApiIdIn(retrievedKey.getRoleId(), permittedApiIdSet);
        Map<String, List<String>> roleAuthedFabIdByApiIdMap = new HashMap<>();
        roleAuthedList.forEach(
                cRoleAuthority -> {
                    roleAuthedFabIdByApiIdMap.computeIfAbsent(cRoleAuthority.getApiId(), k -> new ArrayList<>()).add(cRoleAuthority.getFabId());
                }
        );

        for (SimplePermissionWithIdDto permission : permissionWithIdList) {
            apiIdToSimplePermissionMap.put(permission.getApiId(), permission);
            systemNameToIteapiMap.computeIfAbsent(permission.getSystemName(), k -> new ArrayList<>());
        }

        for (String apiId : permittedApiIdSet) {
            SimplePermissionWithIdDto simplePermission = apiIdToSimplePermissionMap.get(apiId);
            if (simplePermission != null) {
                Web_ModifyApikeyPermissionItemOutDto item = Web_ModifyApikeyPermissionItemOutDto.builder()
                        .systemId(simplePermission.getSystemId())
                        .systemName(simplePermission.getSystemName())
                        .apiId(simplePermission.getApiId())
                        .apiName(simplePermission.getApiName())
                        .totalAuthorizedScopes(roleAuthedFabIdByApiIdMap.get(apiId))
                        .selectedScopes(keyPermittedFabIdByApiIdMap.get(apiId))
                        .apiGatewayEndpointListByFabId(getPresentApiGwEndpointList(simplePermission, keyPermittedFabIdByApiIdMapWithSandbox.get(apiId)))
                        .build();
                systemNameToIteapiMap.get(simplePermission.getSystemName()).add(item);
            }
        }

        return systemNameToIteapiMap;
    }

    private Map<String, List<Web_ApiEndpointDataDetailDto>> getPresentApiGwEndpointList(SimplePermissionWithIdDto simplePermission,
                                                                                       Collection<String> fabIds) throws DataSourceAccessException {
        return getPresentApiGwEndpointList(simplePermission.getSystemName(), simplePermission.getApiId(), fabIds);
    }

    private Map<String, List<Web_ApiEndpointDataDetailDto>> getPresentApiGwEndpointList(String systemName,
                                                                                       String apiId,
                                                                                       Collection<String> fabIds) throws DataSourceAccessException {
        Map<String, List<Web_ApiEndpointDataDetailDto>> resultMap = new HashMap<>();

        List<ApiEndpointEntity> endpointDataList = apiEndpointEntityRepo.findByApiId(apiId);

        if (CollectionUtils.isNotEmpty(endpointDataList)) {
            for (String fabId : fabIds) {
                boolean isProductionFab = validateService.validateIsNotSandBoxFab(fabId);
                ExternalGatewayInfoDto gwInfo = gatewayInfoServiceForHost.getSingleExternalGatewayInfoByFabId(fabId);
                for (ApiEndpointEntity endpoint : endpointDataList) {
                    resultMap.computeIfAbsent(
                            fabId, k -> new ArrayList<>()
                    ).add(
                            Web_ApiEndpointDataDetailDto.builder()
                                    .isProduction(isProductionFab)
                                    .apiItfType(endpoint.getApiItfType())
                                    .apiGwRoute(
                                            GatewayFormatter.formatGwCompleteRoutePath(
                                                    gwInfo.isEnableHttps(),
                                                    gwInfo.getExtGatewayHost(),
                                                    gwInfo.getExtGatewayPort(),
                                                    fabId,
                                                    systemName,
                                                    endpoint.getApiGwUri()
                                            )
                                    )
                                    .apiAcceptMethodList(Arrays.asList(endpoint.getHttpMethod().split("\\s*,\\s*")))
                                    .build()
                    );
                }
            }
        }
        return resultMap;
    }

    public Web_ApiGwEndpointDataDto findApiGwEndpointListByApiIdAndFabIds(String apiId, Collection<String> scopes) throws DataSourceAccessException {
        ApiEntity onSearchApi = validateService.validateApiByApiId(apiId);
        SystemEntity onSearchSystem = validateService.validateSystemId(onSearchApi.getSystemId());
        return Web_ApiGwEndpointDataDto.builder()
                .systemId(onSearchSystem.getSystemId())
                .systemName(onSearchSystem.getSystemName())
                .apiId(onSearchApi.getApiId())
                .apiName(onSearchApi.getApiName())
                .apiGatewayEndpointListByFabId(getPresentApiGwEndpointList(onSearchSystem.getSystemName(), onSearchApi.getApiId(), scopes))
                .build();
    }
}
