package tw.amer.cia.core.service.client;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.component.functional.gateway.GatewayControlHelper;
import tw.amer.cia.core.component.structural.annotation.ClientService;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.exception.gateway.GatewayControllerException;
import tw.amer.cia.core.model.database.RoleDeviceEntity;
import tw.amer.cia.core.model.database.dao.RoleAuthorityEntityRepo;
import tw.amer.cia.core.model.database.dao.RoleDeviceEntityRepo;
import tw.amer.cia.core.model.database.dao.GwRouteEntityRepo;
import tw.amer.cia.core.model.database.RoleAuthorityEntity;
import tw.amer.cia.core.service.core.ValidateService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotBlank;
import java.util.*;
import java.util.stream.Collectors;

@ClientService
public class RoleDeviceServiceForClient {
    @Autowired
    ValidateService validateService;

    @Autowired
    RoleDeviceEntityRepo roleDeviceEntityRepo;

    @Autowired
    RoleAuthorityEntityRepo roleAuthorityEntityRepo;

    @Autowired
    GwRouteEntityRepo gwRouteEntityRepo;

    @Autowired
    GatewayControlHelper gatewayControlHelper;

    public void createOrUpdateRoleDevice(RoleDeviceEntity inDto) throws DataSourceAccessException, GatewayControllerException {
        validateService.validateRoleId(inDto.getRoleId());

        if (StringUtils.isNotBlank(inDto.getDeviceId())) {
            Optional<RoleDeviceEntity> inSearchDevice = roleDeviceEntityRepo.findById(inDto.getDeviceId());

            // 需要事先紀錄，是否有IP需要從Gateway拿掉
            String onDeleteOriginalIp = "";
            RoleDeviceEntity onUpdateDevice;
            if (inSearchDevice.isPresent()) {
                // 更新作業
                onUpdateDevice = inSearchDevice.get();
                onDeleteOriginalIp = StringUtils.equals(onUpdateDevice.getDeviceIp(), inDto.getDeviceIp()) ?
                        onDeleteOriginalIp : onUpdateDevice.getDeviceIp();
                BeanUtils.copyNonNullProperties(inDto, onUpdateDevice);

            } else {
                onUpdateDevice = new RoleDeviceEntity();
                BeanUtils.copyNonNullProperties(inDto, onUpdateDevice);
            }
            roleDeviceEntityRepo.saveAndFlush(onUpdateDevice);

            // update on apisix
            Map<String, HashSet<String>> onUpdateApiIdByFabIdMap = prepareApiIdSetByFabIdMapForGatewayUpdateFromRoleId(inDto.getRoleId());
            if (MapUtils.isNotEmpty(onUpdateApiIdByFabIdMap)) {

                // prepare data
                List<String> onGrantDeviceIpList = Arrays.asList(onUpdateDevice.getDeviceIp());
                List<String> onRevokeDeviceIpList = new ArrayList<>();
                if (StringUtils.isNotBlank(onDeleteOriginalIp)) {
                    onRevokeDeviceIpList.add(onDeleteOriginalIp);
                }


                // Execution
                for (String fabId : onUpdateApiIdByFabIdMap.keySet()) {
                    List<String> gwRouteIdList = gwRouteEntityRepo.findGwRouteIdByFabIdAndApiIdIn(fabId, onUpdateApiIdByFabIdMap.get(fabId));
                    gatewayControlHelper.patchGwRouteDeviceIpListBatch(fabId, gwRouteIdList, onGrantDeviceIpList, onRevokeDeviceIpList);
                }
            }
        } else {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.GENERAL_API_MISSING_CRITICAL_DATA_INPUT.getCompleteMessage()
            );
        }
    }

    public void deleteRoleDeviceById(@NotBlank String deviceId) throws GatewayControllerException {
        if (StringUtils.isNotBlank(deviceId)) {
            Optional<RoleDeviceEntity> inSearchDevice = roleDeviceEntityRepo.findById(deviceId);

            if (inSearchDevice.isPresent()) {
                RoleDeviceEntity onDeleteDevice = inSearchDevice.get();

                // 優先進行 Gateway 清理動作
                Map<String, HashSet<String>> onUpdateApiIdByFabIdMap = prepareApiIdSetByFabIdMapForGatewayUpdateFromRoleId(onDeleteDevice.getRoleId());
                if (MapUtils.isNotEmpty(onUpdateApiIdByFabIdMap)) {

                    // prepare data
                    List<String> onRevokeDeviceIpList = Arrays.asList(onDeleteDevice.getDeviceIp());
                    // Execution
                    for (String fabId : onUpdateApiIdByFabIdMap.keySet()) {
                        List<String> gwRouteIdList = gwRouteEntityRepo.findGwRouteIdByFabIdAndApiIdIn(fabId, onUpdateApiIdByFabIdMap.get(fabId));
                        gatewayControlHelper.patchGwRouteDeviceIpListBatch(fabId, gwRouteIdList, null, onRevokeDeviceIpList);
                    }
                }

            }
        }
    }

    private Map<String, HashSet<String>> prepareApiIdSetByFabIdMapForGatewayUpdateFromRoleId(@NotBlank String roleId) {
        Map<String, HashSet<String>> apiIdSetByFabIdMap = new HashMap<>();
        List<RoleAuthorityEntity> roleAuthorities = roleAuthorityEntityRepo.findByRoleId(roleId);
        if (CollectionUtils.isNotEmpty(roleAuthorities)) {
            // Prepare Data
            roleAuthorities.stream().forEach(
                    cRoleAuthority -> {
                        apiIdSetByFabIdMap.computeIfAbsent(
                                cRoleAuthority.getFabId(), k -> new HashSet<>()
                        ).add(cRoleAuthority.getApiId());
                    }
            );
        }
        return apiIdSetByFabIdMap;
    }

    public boolean initClientRoleDeviceDeploy(List<RoleDeviceEntity> roleDeviceList, List<RoleAuthorityEntity> roleAuthorityList) throws DataSourceAccessException, GatewayControllerException {
        boolean isProcedureSuccess = true;
        // 儲存
        roleDeviceEntityRepo.saveAll(roleDeviceList);

        // Prepare Data
        // 將所有Role Authority 按照 FAB_ID 進行分類 --> onUpdateRoleAuthoritiesByFabIdMap
        Map<String, HashSet<RoleAuthorityEntity>> onUpdateRoleAuthoritiesByFabIdMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(roleAuthorityList)) {
            // Prepare Data
            roleAuthorityList.stream().forEach(
                    cRoleAuthority -> {
                        onUpdateRoleAuthoritiesByFabIdMap.computeIfAbsent(
                                cRoleAuthority.getFabId(), k -> new HashSet<>()
                        ).add(cRoleAuthority);
                    }
            );
        }


        // 將所有 Role Device 按照 FAB_ID 進行分類 --> deviceListByFabIdMap
        Map<String, List<RoleDeviceEntity>> deviceListByFabIdMap = new HashMap<>();
        roleDeviceList.forEach(
                roleDevice -> {
                    deviceListByFabIdMap.computeIfAbsent(
                            roleDevice.getFabId(), k -> new ArrayList<>()
                    ).add(roleDevice);
                }
        );

        // 針對每一個FAB 開始進行處理
        for (String fabId : deviceListByFabIdMap.keySet()) {
            // 處理資料 --> 最後需要通過 ApiId 進行分類，中間需要透過 Role Authorities轉換
            // 將 Fab 內的 Role Device 按照 ROLE_ID  進行分類 --> inFabRoleDeviceByRoleIdMap
            Map<String, Set<RoleDeviceEntity>> inFabRoleDeviceByRoleIdMap = new HashMap<>();
            deviceListByFabIdMap.get(fabId).forEach(
                    roleDevice -> {
                        inFabRoleDeviceByRoleIdMap.computeIfAbsent(
                                roleDevice.getRoleId(), k -> new HashSet<>()
                        ).add(roleDevice);
                    }
            );

            // 將 Fab 內的 Role Device 按照 MS_ID  進行分類 --> inFabRoleDeviceByApiIdMap
            Map<String, Set<RoleDeviceEntity>> inFabRoleDeviceByApiIdMap = new HashMap<>();
            // 取得 FAB 內的所有權限資料
            onUpdateRoleAuthoritiesByFabIdMap.get(fabId).forEach(
                    roleAuthority -> {
                        // 若該權限的擁有者( Role )，有包含在需要更新的設備清單中
                        // 則代表該權限中的微服務 需要在Gateway上面更新設備清單
                        // --> 將該擁有者( Role) 在該 Fab 的設備清單 加入 inFabRoleDeviceByApiIdMap
                        if(inFabRoleDeviceByRoleIdMap.containsKey(roleAuthority.getRoleId())){
                            inFabRoleDeviceByApiIdMap.computeIfAbsent(
                                    roleAuthority.getApiId(), k -> new HashSet<>()
                            ).addAll(inFabRoleDeviceByRoleIdMap.get(roleAuthority.getRoleId()));
                        }
                    }
            );
            // 進行處理，針對FAB 內的每一個 微服務：
            for (String apiId : inFabRoleDeviceByApiIdMap.keySet()) {
                // 尋找 微服務 在 API GATEWAY 上面的 ROUTE_ID ( 依照微服務端口數量，可能有多個 )
                List<String> gwRouteIdList = gwRouteEntityRepo.findGwRouteIdByFabIdAndApiIdIn(fabId, Arrays.asList(apiId));

                // 一次進行 Patch 處理：
                isProcedureSuccess &= gatewayControlHelper.patchGwRouteDeviceIpListBatch(fabId, gwRouteIdList,
                        inFabRoleDeviceByApiIdMap.get(apiId).stream().map(RoleDeviceEntity::getDeviceIp).collect(Collectors.toList()),
                        null);
                if (!isProcedureSuccess) {
                    throw DataSourceAccessException.createExceptionForHttp(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            ErrorConstantLib.UNKNOWN_EXCEPTION_CLIENT_INIT_FAIL.getCompleteMessage()
                                    + "\n" + "While initializing Role Device: FabId = " + fabId + ", ApiId = " + apiId
                    );
                }
            }
        }
        return true;
    }
}
