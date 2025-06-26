package tw.amer.cia.core.service.host.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.common.SuccessConstantLib;
import tw.amer.cia.core.common.WebConstantLib;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.*;
import tw.amer.cia.core.model.database.dao.*;
import tw.amer.cia.core.model.pojo.service.common.apikey.CreateApikeyAcrossFabHostDto;
import tw.amer.cia.core.model.pojo.service.common.apikey.DeleteApikeyHostDto;
import tw.amer.cia.core.model.pojo.service.common.apikey.UpdateApikeyAcrossFabHostDto;
import tw.amer.cia.core.model.pojo.service.host.web.apiMall.Web_BasicApiCardAuthedOutDto;
import tw.amer.cia.core.model.pojo.service.host.web.apiMall.Web_BasicApiCardOutDto;
import tw.amer.cia.core.model.pojo.service.host.web.apiMall.Web_BasicSystemCardOutDto;
import tw.amer.cia.core.model.pojo.service.host.web.apikeyManagement.Web_ApiGwEndpointDataDto;
import tw.amer.cia.core.model.pojo.service.host.web.info.detail.Web_DetailApiInfoOutDto;
import tw.amer.cia.core.model.pojo.service.host.web.info.detail.Web_DetailSystemInfoOutDto;
import tw.amer.cia.core.model.pojo.service.host.web.item.Web_BasicSiteItemAuthedDto;
import tw.amer.cia.core.model.pojo.service.host.web.item.Web_BasicSiteItemDto;
import tw.amer.cia.core.model.pojo.service.host.web.manager.apiManagement.Web_ApiBasicInfoOutDto;
import tw.amer.cia.core.service.core.ValidateService;
import tw.amer.cia.core.service.host.ApikeyServiceForHost;
import tw.amer.cia.core.service.host.RoleServiceForHost;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@HostService
public class HostWebFrontApiService {
    @Autowired
    ValidateService validateService;
    @Autowired
    SystemEntityRepo systemEntityRepo;

    @Autowired
    SystemDpyEntityRepo systemDpyEntityRepo;

    @Autowired
    ApiEntityRepo apiEntityRepo;

    @Autowired
    ApiDpyEntityRepo apiDpyEntityRepo;

    @Autowired
    RoleAuthorityEntityRepo roleAuthorityEntityRepo;

    @Autowired
    UserEntityRepo userEntityRepo;

    @Autowired
    GwApikeyEntityRepo gwApikeyEntityRepo;

    @Autowired
    GwApikeyPermissionRepo gwApikeyPermissionRepo;

    @Autowired
    ApikeyServiceForHost apikeyServiceForHost;

    @Autowired
    RoleServiceForHost roleServiceForHost;

    @Autowired
    RoleAuthApplyEntityRepo rApyRoleAuthRepo;

    // Query
    public Page<Web_BasicSystemCardOutDto> webFindSystemCardAll(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(SystemEntity.Fields.systemName).ascending());
        Page<SystemEntity> sourcePage = systemEntityRepo.findAllByOrderBySystemNameAsc(pageable);

        List<SystemEntity> sourceList = sourcePage.getContent();
        if (CollectionUtils.isNotEmpty(sourceList)) {
            List<SystemDpyEntity> dpyList = systemDpyEntityRepo.findBySystemIds(sourceList.stream().map(SystemEntity::getSystemId).collect(Collectors.toList()));

            // 資料整理，並忽略虛擬FAB
            Map<String, List<String>> fabIdListBySystemId = dpyList.stream()
                    .filter(dpy -> StringUtils.isNotEmpty(dpy.getFabId()))
                    .filter(dpy -> validateService.validateIsNotSandBoxFab(dpy.getFabId()))
                    .collect(Collectors.groupingBy(SystemDpyEntity::getSystemId,
                            Collectors.mapping(SystemDpyEntity::getFabId, Collectors.toList())));

            List<Web_BasicSystemCardOutDto> systemCards = sourceList.stream()
                    .map(cSysSystem -> {
                        Web_BasicSystemCardOutDto systemCard = new Web_BasicSystemCardOutDto();
                        BeanUtils.copyNonNullProperties(cSysSystem, systemCard);
                        systemCard.setOwnerName(findUserNameByWorkId(cSysSystem.getOwner()));
                        systemCard.setSiteList(
                                Web_BasicSiteItemDto.importFromMap(
                                        fabIdListTransferTositeFabIdMap(
                                                fabIdListBySystemId.getOrDefault(cSysSystem.getSystemId(), Collections.emptyList()))
                                ));
                        return systemCard;
                    })
                    .collect(Collectors.toList());

            return new PageImpl<>(systemCards, sourcePage.getPageable(), sourcePage.getTotalElements());
        }
        return new PageImpl<>(Collections.emptyList());
    }

    public Web_DetailSystemInfoOutDto webGetSystemDetailInfo(String systemId) throws DataSourceAccessException {
        // 基本資訊先全部拿到
        SystemEntity system = validateService.validateSystemId(systemId);
        List<SystemDpyEntity> dpyList = systemDpyEntityRepo.findBySystemId(systemId);

        // 資料整理，並忽略虛擬FAB
        Web_DetailSystemInfoOutDto outDto = new Web_DetailSystemInfoOutDto();
        BeanUtils.copyNonNullProperties(system, outDto);
        outDto.setSiteList(
                Web_BasicSiteItemDto.importFromMap(
                        fabIdListTransferTositeFabIdMap(
                                dpyList.stream()
                                        .map(SystemDpyEntity::getFabId)
                                        .filter(fabId -> validateService.validateIsNotSandBoxFab(fabId))
                                        .collect(Collectors.toList()))
                ));

        // Set OwnerName
        if (StringUtils.isNotBlank(outDto.getOwner())) {
            String ownerName = findUserNameByWorkId(outDto.getOwner());
            outDto.setOwnerName(ownerName);
        }


        return outDto;
    }


    private String findUserNameByWorkId(String userId) {
        Optional<UserEntity> onSearchUsr = userEntityRepo.findById(userId);
        if (onSearchUsr.isPresent()) {
            return onSearchUsr.get().getUserName();
        } else {
            return "Unknown Member";
        }
    }

    public Web_DetailApiInfoOutDto webGetApiDetailInfo(String roleId, String apiId) throws DataSourceAccessException {
        // 基本資訊先全部拿到
        ApiEntity api = validateService.validateApiByApiId(apiId);
        SystemEntity system = validateService.validateSystemId(api.getSystemId());

        // 微服務部署資訊取得
        List<ApiDpyEntity> totalApiDpyList = apiDpyEntityRepo.findByApiId(apiId);

        // ROLE 針對 微服務的權限 資料取得
        List<RoleAuthorityEntity> roleAuthorityList = roleAuthorityEntityRepo.findByRoleIdAndApiId(roleId, apiId);

        Web_DetailApiInfoOutDto outDto = new Web_DetailApiInfoOutDto();

        // 注入相關資料
        BeanUtils.copyNonNullProperties(system, outDto);
        BeanUtils.copyNonNullProperties(api, outDto);

        // 注入權限資料，並忽略虛擬FAB
        outDto.setSiteAuthedList(
                Web_BasicSiteItemAuthedDto.importFromDeployedFabAndAuthorizedFab(
                        fabIdListTransferTositeFabIdMap(
                                totalApiDpyList.stream()
                                        .map(ApiDpyEntity::getFabId)
                                        .filter(fabId -> validateService.validateIsNotSandBoxFab(fabId))
                                        .collect(Collectors.toList())),
                        roleAuthorityList.stream()
                                .map(RoleAuthorityEntity::getFabId)
                                .collect(Collectors.toList()))
        );
        return outDto;
    }

    public Web_ApiBasicInfoOutDto webGetBasicApiInfoById(String apiId) throws DataSourceAccessException {
        // 基本資訊先全部拿到
        ApiEntity api = validateService.validateApiByApiId(apiId);
        SystemEntity system = validateService.validateSystemId(api.getSystemId());

        Web_ApiBasicInfoOutDto outDto = new Web_ApiBasicInfoOutDto();

        // 注入相關資料
        BeanUtils.copyNonNullProperties(system, outDto);
        BeanUtils.copyNonNullProperties(api, outDto);

        return outDto;
    }

    public List<Web_BasicApiCardAuthedOutDto> webFindApiCardBySystemId(String roleId, String systemId) throws DataSourceAccessException {
        // 基本資訊先全部拿到
        SystemEntity system = validateService.validateSystemId(systemId);
        List<ApiEntity> totalApiList = apiEntityRepo.findBySystemId(systemId);
        List<String> totalApiIdList = totalApiList.stream().map(ApiEntity::getApiId).collect(Collectors.toList());

        // 微服務部署資訊取得，並忽略虛擬FAB
        List<ApiDpyEntity> totalApiDpyList =
                apiDpyEntityRepo.findByApiIdIn(totalApiIdList).stream()
                        .filter(dpy -> validateService.validateIsNotSandBoxFab(dpy.getFabId()))
                        .collect(Collectors.toList());

        Map<String, List<String>> apiDpyByApiIdMap = new HashMap<>();
        for (ApiDpyEntity apiDpy : totalApiDpyList) {
            if (apiDpyByApiIdMap.containsKey(apiDpy.getApiId())) {
                apiDpyByApiIdMap.get(apiDpy.getApiId()).add(apiDpy.getFabId());
            } else {
                apiDpyByApiIdMap.put(apiDpy.getApiId(), Stream.of(apiDpy.getFabId()).collect(Collectors.toList()));
            }
        }
        // ROLE 針對 微服務的權限 資料取得
        List<RoleAuthorityEntity> roleAuthorityList =
                new ArrayList<>(roleAuthorityEntityRepo.findByRoleIdAndApiIdIn(roleId, totalApiIdList));
        Map<String, List<String>> roleAuthorityByApiIdMap = new HashMap<>();
        for (RoleAuthorityEntity roleAuthority : roleAuthorityList) {
            if (roleAuthorityByApiIdMap.containsKey(roleAuthority.getApiId())) {
                roleAuthorityByApiIdMap.get(roleAuthority.getApiId()).add(roleAuthority.getFabId());
            } else {
                roleAuthorityByApiIdMap.put(roleAuthority.getApiId(), Stream.of(roleAuthority.getFabId()).collect(Collectors.toList()));
            }
        }

        // 取得 申請中微服務清單
        Set<String> onAppliedApiIdSet = new HashSet<>(rApyRoleAuthRepo.findOnAppliedApiIdListByRole(roleId));

        if (CollectionUtils.isNotEmpty(totalApiList)) {
            return totalApiList.parallelStream().map(apiEntity -> {
                        Web_BasicApiCardAuthedOutDto apiCardDto = Web_BasicApiCardAuthedOutDto.builder()
                                .systemId(system.getSystemId())
                                .systemName(system.getSystemName())
                                .siteAuthedList(
                                        Web_BasicSiteItemAuthedDto.importFromDeployedFabAndAuthorizedFab(
                                                fabIdListTransferTositeFabIdMap(apiDpyByApiIdMap.get(apiEntity.getApiId())),
                                                roleAuthorityByApiIdMap.get(apiEntity.getApiId())
                                        )
                                )
                                .build();
                        // 覆蓋 API 資訊
                        BeanUtils.copyNonNullProperties(apiEntity, apiCardDto);

                        // 確認微服務權限狀態
                        if (apiCardDto.getSiteAuthedList().stream()
                                .flatMap(siteItem -> siteItem.getFabMap().values().stream())
                                .allMatch(authed -> authed)) {  // 先檢查是否所有資訊都取得了
                            apiCardDto.setRoleAuthedStatus(WebConstantLib.WEB_UI_API_CARD_ROLE_AUTHED_STATUS.TOTALLY_OBTAINED);
                        } else if (onAppliedApiIdSet.contains(apiEntity.getApiId())) { // 接下來檢查是否有資訊正在申請
                            apiCardDto.setRoleAuthedStatus(WebConstantLib.WEB_UI_API_CARD_ROLE_AUTHED_STATUS.ON_APPLYING);
                        } else {
                            apiCardDto.setRoleAuthedStatus(WebConstantLib.WEB_UI_API_CARD_ROLE_AUTHED_STATUS.OPEN_FOR_APPLY);
                        }
                        return apiCardDto;
                    })
                    // 改成先排序Flag再排名稱
                    .sorted(Comparator.comparing(Web_BasicApiCardAuthedOutDto::getApplicableFlag, Comparator.reverseOrder())
                            .thenComparing(Web_BasicApiCardAuthedOutDto::getApiName))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public Page<GwApikeyEntity> findVisibleApikeyByRoleIdOrderByKeyNameAsc(String roleId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(GwApikeyEntity.Fields.keyName).ascending());
        Page<GwApikeyEntity> sourcePage = gwApikeyEntityRepo.findByRoleIdAndIsUiVisibleOrderByKeyNameAsc(roleId, GeneralSetting.GENERAL_POSITIVE_STRING, pageable);
        if (CollectionUtils.isNotEmpty(sourcePage.getContent())) {
            return sourcePage;
        }
        return new PageImpl<>(Collections.emptyList());
    }

    public List<Web_BasicSiteItemAuthedDto> findApikeyEffectedScopeByRoleIdAndApikeyId(String roleId, String apikeyId) throws DataSourceAccessException {
        validateService.validateApikeyByApikeyId(apikeyId);

        Set<String> totalScopeList = roleAuthorityEntityRepo.findByRoleId(roleId).stream().map(RoleAuthorityEntity::getFabId).collect(Collectors.toSet());
        Set<String> effectedScopeList = gwApikeyPermissionRepo.findByApikeyId(apikeyId).stream().map(GwApikeyPermissionEntity::getFabId).collect(Collectors.toSet());

        return Web_BasicSiteItemAuthedDto.importFromDeployedFabAndAuthorizedFab(
                fabIdListTransferTositeFabIdMap(totalScopeList),
                effectedScopeList
        );

    }

    public Object updateApikeyPermissionByScope(String roleId, String apikeyId, List<String> enableScopeList, List<String> disableScopeList) throws DataSourceAccessException, CiaProcessorException {
        enableScopeList = CollectionUtils.isNotEmpty(enableScopeList) ? enableScopeList : Collections.emptyList();
        disableScopeList = CollectionUtils.isNotEmpty(disableScopeList) ? disableScopeList : Collections.emptyList();

        apikeyServiceForHost.updateApikeyPermissionByApikeyIdAndScopeList(roleId, apikeyId, enableScopeList, disableScopeList);
        return SuccessConstantLib.MODIFY_KEY_SCOPE_SUCCESS.getCompleteMessage();
    }

    public Object updateApikeyActiveStatus(String apikeyId) throws DataSourceAccessException, CiaProcessorException {
        apikeyServiceForHost.updateApikeyStatus(apikeyId);
        return SuccessConstantLib.MODIFY_KEY_ACTIVE_STATUS_SUCCESS.getCompleteMessage();
    }

    public Object findRoleAuthorityScopeByRoleId(String roleId) {
        Set<String> fabIdList = roleServiceForHost.findRoleAuthorityScopeByRoleId(roleId);
        return Web_BasicSiteItemDto.importFromMap(fabIdListTransferTositeFabIdMap(fabIdList));
    }

    public Map<String, List<String>> fabIdListTransferTositeFabIdMap(Collection<String> fabIdList) {
        Map<String, List<String>> resultMap = new HashMap<>();
        try {
            if (CollectionUtils.isNotEmpty(fabIdList)) {
                for (String fabId : fabIdList) {
                    String site = validateService.validateFabIdExistsReturnSite(fabId);
                    if (resultMap.containsKey(site)) {
                        resultMap.get(site).add(fabId);
                    } else {
                        resultMap.put(site, new ArrayList<String>() {{
                            add(fabId);
                        }});
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return resultMap;
    }

    public List<Web_BasicSystemCardOutDto> findRoleAuthoritySystemByRoleIdAndFabIdList(String roleId, List<String> scopeList) {

        List<SystemEntity> systemList = roleServiceForHost.findRoleAuthoritySystemByRoleIdAndFabIdList(roleId, scopeList);
        if (CollectionUtils.isNotEmpty(systemList)) {
            return systemList.stream()
                    .map(cSysSystem -> {
                        Web_BasicSystemCardOutDto systemCard = new Web_BasicSystemCardOutDto();
                        BeanUtils.copyNonNullProperties(cSysSystem, systemCard);
                        systemCard.setSiteList(
                                Web_BasicSiteItemDto.importFromMap(fabIdListTransferTositeFabIdMap(scopeList))
                        );
                        return systemCard;
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public Object findRoleAuthorityApiByRoleIdAndSystemIdAndFabIdList(String roleId, String systemId, List<String> scopeList) throws DataSourceAccessException {
        SystemEntity system = validateService.validateSystemId(systemId);

        List<ApiEntity> apiList = roleServiceForHost.findRoleAuthorityApiByRoleIdAndSystemIdAndFabIdList(roleId, systemId, scopeList);
        List<ApiDpyEntity> apiDpyList = apiDpyEntityRepo.findByApiIdIn(apiList.stream().map(ApiEntity::getApiId).collect(Collectors.toList())).stream()
                .filter(dpy -> validateService.validateIsNotSandBoxFab(dpy.getFabId()))
                .collect(Collectors.toList());


        Map<String, List<String>> fabIdListByApiIdMap = new HashMap<>();
        apiDpyList.forEach(
                apiDpyEntity -> {
                    fabIdListByApiIdMap.computeIfAbsent(
                            apiDpyEntity.getApiId(), k -> new ArrayList<>()
                    ).add(apiDpyEntity.getFabId());
                }
        );

        if (CollectionUtils.isNotEmpty(apiList)) {
            return apiList.stream().distinct().map(apiEntity -> {
                Web_BasicApiCardOutDto apiCardDto = Web_BasicApiCardOutDto.builder()
                        .systemId(system.getSystemId())
                        .systemName(system.getSystemName())
                        .siteDeployedList(
                                Web_BasicSiteItemAuthedDto.importFromDeployedFabAndAuthorizedFab(
                                        fabIdListTransferTositeFabIdMap(fabIdListByApiIdMap.get(apiEntity.getApiId())),
                                        roleAuthorityEntityRepo.findByRoleIdAndApiId(roleId, apiEntity.getApiId()).stream()
                                                .map(RoleAuthorityEntity::getFabId)
                                                .collect(Collectors.toList())
                                )
                        )
                        .build();
                BeanUtils.copyNonNullProperties(apiEntity, apiCardDto);
                return apiCardDto;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public Object duplicateApikeyWithKeyName(String roleId, String sourceApikeyId, String newApikeyName) throws DataSourceAccessException, CiaProcessorException {
        return apikeyServiceForHost.duplicateApikeyWithKeyName(roleId, sourceApikeyId, newApikeyName);
    }

    public Object deleteApikey(String roleId, String apikeyId) throws DataSourceAccessException, CiaProcessorException {
        apikeyServiceForHost.deleteApikeyFromHost(DeleteApikeyHostDto.builder()
                .roleId(roleId)
                .apikeyId(apikeyId).build());
        return apikeyId;
    }

    public Object createApikey(String roleId, String newApikeyName, String newApikeyDesc, Map<String, List<String>> fabIdApiListMap) throws DataSourceAccessException, CiaProcessorException {
        return apikeyServiceForHost.createApikeyFromHostAcrossFab(
                CreateApikeyAcrossFabHostDto.builder()
                        .roleId(roleId)
                        .keyName(newApikeyName)
                        .keyDesc(newApikeyDesc)
                        .fabIdApiListMap(fabIdApiListMap)
                        .build()
        );
    }

    public Object findApikeyFunction(String apikeyId) throws DataSourceAccessException {
        return apikeyServiceForHost.retrieveApikeyPermissionByApikeyId(apikeyId);
    }

    public Object updateApikeyFunction(String roleId, String apikeyId, Map<String, List<String>> fabIdApiListMap) throws DataSourceAccessException, CiaProcessorException {
        apikeyServiceForHost.updateApikeyPermissionFromHostAcrossFab(
                UpdateApikeyAcrossFabHostDto.builder()
                        .roleId(roleId)
                        .apikeyId(apikeyId)
                        .fabIdApiListMap(fabIdApiListMap)
                        .build());
        return apikeyId;
    }

    public Web_ApiGwEndpointDataDto findApiGwEndpointListByApiIdAndScopes(String apiId, Collection<String> scopes) throws DataSourceAccessException {
        return apikeyServiceForHost.findApiGwEndpointListByApiIdAndFabIds(apiId, scopes);
    }
}
