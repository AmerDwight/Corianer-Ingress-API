package tw.amer.cia.core.service.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import tw.amer.cia.core.common.DataModifyAction;
import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.component.functional.coriander.CallHostApiComponent;
import tw.amer.cia.core.component.functional.gateway.GatewayControlHelper;
import tw.amer.cia.core.component.structural.annotation.ClientService;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.exception.gateway.ApisixProcessorException;
import tw.amer.cia.core.exception.gateway.GatewayControllerException;
import tw.amer.cia.core.model.database.GwApikeyEntity;
import tw.amer.cia.core.model.database.GwApikeyPermissionEntity;
import tw.amer.cia.core.model.database.dao.GwApikeyEntityRepo;
import tw.amer.cia.core.model.database.dao.GwApikeyPermissionRepo;
import tw.amer.cia.core.model.database.dao.GwRouteEntityRepo;
import tw.amer.cia.core.model.pojo.service.common.apikey.DeleteApikeyClientDto;
import tw.amer.cia.core.model.pojo.service.common.apikey.UpdateApikeyClientDto;
import tw.amer.cia.core.service.core.ValidateService;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@ClientService
@Slf4j
public class ApikeyServiceForClient {
    @Autowired
    ValidateService validateService;
    @Autowired
    GatewayControlHelper gatewayControlHelper;
    @Autowired
    GwApikeyEntityRepo gwApikeyEntityRepo;
    @Autowired
    GwApikeyPermissionRepo gwApikeyPermissionRepo;
    @Autowired
    GwRouteEntityRepo gwRouteEntityRepo;
    @Autowired
    CallHostApiComponent callHostApiComponent;

    @Transactional(rollbackFor = {Exception.class})
    public boolean updateApikeyFromClient(UpdateApikeyClientDto dto) throws DataSourceAccessException {

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
            for (String onGrantApiId : new HashSet<>(dto.getApiIdList())) {
                GwApikeyPermissionEntity rKeyPermission;
                Optional<GwApikeyPermissionEntity> inSearchKeyPermission = gwApikeyPermissionRepo.findByFabIdAndApikeyIdAndApiId(dto.getFabId(), onModifyApikey.getApikeyId(), onGrantApiId);
                if (inSearchKeyPermission.isPresent()) {
                    rKeyPermission = inSearchKeyPermission.get();
                } else {
                    rKeyPermission = GwApikeyPermissionEntity.builder()
                            .apikeyId(onModifyApikey.getApikeyId())
                            .fabId(dto.getFabId())
                            .apiId(onGrantApiId)
                            .createTime(Instant.now())
                            .build();
                }
                newPermissionList.add(rKeyPermission);
            }
        }
        // 捕捉差集
        // 1.  愈刪除權限之微服務清單；即原本有，更新後將消失的部分。
        List<GwApikeyPermissionEntity> waitRevokeList = (List<GwApikeyPermissionEntity>) CollectionUtils.subtract(oldPermissionList, newPermissionList);
        // 2.  愈新建權限之微服務清單；即原本沒有，更新後將加入的部分。
        List<GwApikeyPermissionEntity> waitGrantList = (List<GwApikeyPermissionEntity>) CollectionUtils.subtract(newPermissionList, oldPermissionList);

        try {
            boolean revokeSuccess = true;
            boolean grantSuccess = true;

            // Do Grant
            if (CollectionUtils.isNotEmpty(waitGrantList)) {
                grantSuccess = this.grantApikeyPermissionsNoSync(onModifyApikey, dto.getFabId(), waitGrantList);
            }
            // Do Revoke
            if (CollectionUtils.isNotEmpty(waitRevokeList)) {
                revokeSuccess = this.revokeApikeyPermissionsNoSync(onModifyApikey, dto.getFabId(), waitRevokeList);

            }
            // Sync
            callHostApiComponent.updateApikeyPermissionBatchFromClient(onModifyApikey.getApikeyId(), waitRevokeList, waitGrantList);
            this.deleteApikeyIfNoUsageInClientScope(onModifyApikey);

            return revokeSuccess && grantSuccess;
        } catch (Exception e) {
            throw new DataSourceAccessException(e.getMessage());
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public boolean revokeApikeyPermissionsNoSync(GwApikeyEntity onModifyApikey, String fabId, List<GwApikeyPermissionEntity> waitRevokeList) throws GatewayControllerException {
        boolean revokeSuccess = false;
        for (GwApikeyPermissionEntity onRevokePermission : waitRevokeList) {
            List<String> gwRouteIdList = gwRouteEntityRepo.findGwRouteIdByFabIdAndApiId(fabId, onRevokePermission.getApiId());
            for (String gwRouteId : gwRouteIdList) {
                // Revoke Gateway
                revokeSuccess = gatewayControlHelper.revokeGwApikeyPermission(fabId, gwRouteId, onModifyApikey.getRoleId(), onModifyApikey.getKeyName());
            }
            // Revoke Local DB
            gwApikeyPermissionRepo.deleteByFabIdAndApikeyIdAndApiId(fabId, onModifyApikey.getApikeyId(), onRevokePermission.getApiId());
        }
        return revokeSuccess;
    }

    @Transactional(rollbackFor = {Exception.class})
    private boolean grantApikeyPermissionsNoSync(GwApikeyEntity onModifyApikey, String fabId, List<GwApikeyPermissionEntity> waitGrantList) throws GatewayControllerException {
        boolean grantSuccess = false;
        for (GwApikeyPermissionEntity onGrantPermission : waitGrantList) {
            List<String> gwRouteIdList = gwRouteEntityRepo.findGwRouteIdByFabIdAndApiId(fabId, onGrantPermission.getApiId());
            for (String gwRouteId : gwRouteIdList) {
                // Grant on Gateway
                grantSuccess = gatewayControlHelper.grantGwApikeyPermission(fabId, gwRouteId, onModifyApikey.getRoleId(), onModifyApikey.getKeyName());
            }
            gwApikeyPermissionRepo.save(onGrantPermission);
        }
        return grantSuccess;
    }

    @Transactional(rollbackFor = {Exception.class})
    private boolean deleteApikeyIfNoUsageInClientScope(GwApikeyEntity onModifyApikey) throws GatewayControllerException, DataSourceAccessException {
        boolean apikeyPurged = false;

        List<GwApikeyPermissionEntity> onSearchPermissions = gwApikeyPermissionRepo.findByApikeyId(onModifyApikey.getApikeyId());
        if (CollectionUtils.isEmpty(onSearchPermissions)) {
            apikeyPurged = apikeyPurged | this.deleteApikeyFromClient(DeleteApikeyClientDto.builder()
                    .apikeyId(onModifyApikey.getApikeyId())
                    .roleId(onModifyApikey.getRoleId())
                    .build());
        }

        return apikeyPurged;
    }


    @Transactional(rollbackFor = {Exception.class})
    public boolean deleteApikeyFromClient(DeleteApikeyClientDto dto) throws DataSourceAccessException, GatewayControllerException {
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

        boolean revokeSuccess = false;

        if (CollectionUtils.isNotEmpty(keyPermissionList)) {
            // 1. purge permissions - APISIX
            for (GwApikeyPermissionEntity keyPermission : keyPermissionList) {
                List<String> gwRouteList = gwRouteEntityRepo.findGwRouteIdByFabIdAndApiId(keyPermission.getFabId(), keyPermission.getApiId());
                if (CollectionUtils.isNotEmpty((gwRouteList))) {
                    for (String gwRouteId : gwRouteList) {
                        boolean successPurgePermission = gatewayControlHelper.revokeGwApikeyPermission(keyPermission.getFabId(), gwRouteId
                                , waitDeleteApikey.getRoleId(), waitDeleteApikey.getKeyName());
                        if (successPurgePermission) {
                            log.info("Revoke Apikey Permission： " + keyPermission.getFabId() + " ApiId： " + keyPermission.getApiId() + " " +
                                    " Gateway Route： " + gwRouteId + " " +
                                    "Apikey：" + keyPermission.getApikeyId());
                        } else {
                            log.info("Revoke Apikey Permission FAIL!!： " + keyPermission.getFabId() + " ApiId： " + keyPermission.getApiId() + " Apikey：" + keyPermission.getApikeyId());
                            throw ApisixProcessorException.createExceptionForHttp(HttpStatus.INTERNAL_SERVER_ERROR,
                                    ErrorConstantLib.GATEWAY_COMMAND_UNABLE_REVOKE_KEY_PERMISSION.getCompleteMessage());
                        }
                    }
                }
            }
            // 2. C-Table Data
            // Only call CIA restful data api to purge GwApikeyPermissionEntity
            // It shall only purge without any other effect including CIA/ CGC loop deletion.
            callHostApiComponent.updateApikeyPermissionBatchFromClient(waitDeleteApikey.getApikeyId(), keyPermissionList, null);
            gwApikeyPermissionRepo.deleteAll(keyPermissionList);
        }

        // Action 2 purge Apikey
        boolean apikeyDeletedInGw = true;

        apikeyDeletedInGw &= gatewayControlHelper.deleteGwApikey(waitDeleteApikey.getRoleId(), waitDeleteApikey.getKeyName());
        if (apikeyDeletedInGw) {
            // 2. C-Table Data
            callHostApiComponent.deleteApikeyCheckFromClient(waitDeleteApikey.getApikeyId());
            gwApikeyEntityRepo.delete(waitDeleteApikey);
        }
        return true;
    }

    // CIA 系統用Apikey功能
    @Transactional(rollbackFor = {Exception.class})
    public boolean createOrUpdateApikeyBySiteFromHost(GwApikeyEntity apikey) throws GatewayControllerException {
        boolean procedureSuccess = true;

        Optional<GwApikeyEntity> inSearchRole = gwApikeyEntityRepo.findByApikeyId(apikey.getApikeyId());
        if (inSearchRole.isPresent()) {
            GwApikeyEntity localApikeyObject = inSearchRole.get();
            BeanUtils.copyNonNullProperties(apikey, localApikeyObject);
            gwApikeyEntityRepo.save(localApikeyObject);
        } else {
            gwApikeyEntityRepo.save(apikey);
        }

        gatewayControlHelper.createGwApikeyBySite(apikey.getRoleId(), apikey.getApikeyId(), apikey.getKeyName());

        return procedureSuccess;
    }

    @Transactional(rollbackFor = {Exception.class})
    public boolean createOrUpdateApikeyPermissionFromHostBatch(List<GwApikeyPermissionEntity> inDto) throws GatewayControllerException {
        boolean grantSuccess = false;
        for (GwApikeyPermissionEntity onGrantPermission : inDto) {
            String fabId = onGrantPermission.getFabId();
            Optional<GwApikeyEntity> onSearchApikey = gwApikeyEntityRepo.findByApikeyId(onGrantPermission.getApikeyId());

            boolean apikeyNotExistsInThisClient = !onSearchApikey.isPresent();
            if (apikeyNotExistsInThisClient) {
                GwApikeyEntity obtainedKey = callHostApiComponent.obtainApikeyByApikeyId(onGrantPermission.getApikeyId());
                if (obtainedKey != null && StringUtils.equalsIgnoreCase(obtainedKey.getApikeyId(), onGrantPermission.getApikeyId())) {
                    createOrUpdateApikeyBySiteFromHost(obtainedKey);
                    onSearchApikey = Optional.of(obtainedKey);
                }
            }
            if (onSearchApikey.isPresent()) {
                GwApikeyEntity onModifyApikey = onSearchApikey.get();
                List<String> gwRouteIdList = gwRouteEntityRepo.findGwRouteIdByFabIdAndApiId(fabId, onGrantPermission.getApiId());
                for (String gwRouteId : gwRouteIdList) {
                    // Grant on Gateway
                    grantSuccess = gatewayControlHelper.grantGwApikeyPermission(fabId, gwRouteId, onModifyApikey.getRoleId(), onModifyApikey.getKeyName());
                }
                // Store In Local DB
                gwApikeyPermissionRepo.save(onGrantPermission);
            }
        }
        return grantSuccess;
    }

    @Transactional(rollbackFor = {Exception.class})
    public boolean revokeApikeyPermissionFromHostBatch(List<GwApikeyPermissionEntity> inDto) throws GatewayControllerException {
        boolean revokeSuccess = false;
        for (GwApikeyPermissionEntity onRevokePermission : inDto) {
            String fabId = onRevokePermission.getFabId();
            Optional<GwApikeyEntity> onSearchApikey = gwApikeyEntityRepo.findByApikeyId(onRevokePermission.getApikeyId());

            if (onSearchApikey.isPresent()) {
                GwApikeyEntity onModifyApikey = onSearchApikey.get();

                List<String> gwRouteIdList = gwRouteEntityRepo.findGwRouteIdByFabIdAndApiId(fabId, onRevokePermission.getApiId());
                for (String gwRouteId : gwRouteIdList) {
                    // Revoke Gateway
                    revokeSuccess = gatewayControlHelper.revokeGwApikeyPermission(fabId, gwRouteId, onModifyApikey.getRoleId(), onModifyApikey.getKeyName());
                }
                // Revoke In Local DB
                gwApikeyPermissionRepo.deleteByFabIdAndApikeyIdAndApiId(fabId, onModifyApikey.getApikeyId(), onRevokePermission.getApiId());
            }
        }

        // Check If Apikey is still in needs
        Set<String> onCheckApikeyIdSet = inDto.stream().map(GwApikeyPermissionEntity::getApikeyId).collect(Collectors.toSet());
        List<GwApikeyEntity> noUsageApikeyList = gwApikeyEntityRepo.findNoUsageApikeyByApikeyIds(onCheckApikeyIdSet);
        if (CollectionUtils.isNotEmpty(noUsageApikeyList)) {
            for (GwApikeyEntity noUsageKey : noUsageApikeyList) {
                // Action 2 purge Apikey
                revokeSuccess &= gatewayControlHelper.deleteGwApikey(noUsageKey.getRoleId(), noUsageKey.getKeyName());
                // C-Table Data
                gwApikeyEntityRepo.delete(noUsageKey);
            }
        }

        return revokeSuccess;
    }

    @Transactional(rollbackFor = {Exception.class})
    public void tryUpdateApikeyActiveStatusBroadcastNoReplyFromHost(String apikeyId) throws ApisixProcessorException {
        Optional<GwApikeyEntity> inSearchKey = gwApikeyEntityRepo.findByApikeyId(apikeyId);
        if (inSearchKey.isPresent()) {
            GwApikeyEntity thisKey = inSearchKey.get();
            String newStatus = StringUtils.equalsIgnoreCase(thisKey.getIsActive(), DataModifyAction.DATA_IS_ACTIVE_TRUE) ?
                    DataModifyAction.DATA_IS_ACTIVE_FALSE : DataModifyAction.DATA_IS_ACTIVE_TRUE;
            thisKey.setIsActive(newStatus);
            gwApikeyEntityRepo.save(thisKey);

            // Do Gateway Operations
            Set<String> fabIdSet = gwApikeyPermissionRepo.findByApikeyId(apikeyId).stream().map(GwApikeyPermissionEntity::getFabId).collect(Collectors.toSet());
            for (String fabId : fabIdSet) {
                gatewayControlHelper.changeApikeyStatus(fabId, thisKey.getRoleId(), thisKey.getApikeyId(), thisKey.getKeyName()
                        , StringUtils.equalsIgnoreCase(thisKey.getIsActive(), DataModifyAction.DATA_IS_ACTIVE_TRUE));
            }

        }
    }
}
