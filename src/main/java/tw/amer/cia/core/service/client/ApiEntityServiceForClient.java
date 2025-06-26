package tw.amer.cia.core.service.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.component.functional.gateway.GatewayControlHelper;
import tw.amer.cia.core.component.structural.annotation.ClientService;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.exception.gateway.GatewayControllerException;
import tw.amer.cia.core.model.database.*;
import tw.amer.cia.core.model.database.compositeId.ApiGwPluginDpyEntityId;
import tw.amer.cia.core.model.database.dao.*;
import tw.amer.cia.core.model.database.dto.GwRouteDto;
import tw.amer.cia.core.model.pojo.component.gateway.GwApikeyNameDto;
import tw.amer.cia.core.model.pojo.service.common.api.ApiNameDto;
import tw.amer.cia.core.model.pojo.service.common.api.CreateOrUpdateApiEndpointDto;
import tw.amer.cia.core.service.client.subProcess.ApiServiceForClientSubProcess;
import tw.amer.cia.core.service.core.ValidateService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ClientService
public class ApiEntityServiceForClient {

    @Autowired
    ValidateService validateService;
    @Autowired
    ApiServiceForClientSubProcess apiServiceForClientSubProcess;
    @Autowired
    ApiEntityRepo apiEntityRepo;
    @Autowired
    ApiEndpointEntityRepo apiEndpointEntityRepo;
    @Autowired
    ApiDpyEntityRepo apiDpyEntityRepo;
    @Autowired
    GwRouteEntityRepo gwRouteEntityRepo;
    @Autowired
    GwUpstreamEntityRepo gwUpstreamEntityRepo;
    @Autowired
    GwApikeyEntityRepo gwApikeyEntityRepo;
    @Autowired
    GatewayControlHelper gatewayControlHelper;
    @Autowired
    GwPluginEntityRepo gwPluginEntityRepo;
    @Autowired
    ApiGwPluginDpyEntityRepo apiGwPluginDpyEntityRepo;

    // CIA，切分開發
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrUpdateApi(ApiEntity api) {
        boolean procedureSuccess = true;
        Optional<ApiEntity> inSearchApi = apiEntityRepo.findByApiId(api.getApiId());
        if (inSearchApi.isPresent()) {
            ApiEntity localApiObject = inSearchApi.get();
            BeanUtils.copyNonNullProperties(api, localApiObject);
            apiEntityRepo.save(localApiObject);
        } else {
            apiEntityRepo.save(api);
        }
        return procedureSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createOrUpdateApiEndpoint(CreateOrUpdateApiEndpointDto inDto) throws GatewayControllerException, DataSourceAccessException {
        boolean procedureSuccess = true;

        if (CollectionUtils.isNotEmpty(inDto.getApiEndpointEntities())) {
            ApiEntity api = inDto.getApiEntity();
            List<ApiEndpointEntity> newEndpointList = inDto.getApiEndpointEntities();
            List<ApiEndpointEntity> existsEndpointList = apiEndpointEntityRepo.findByApiId(api.getApiId());
            boolean isEndpointListChanged = !(CollectionUtils.isEqualCollection(newEndpointList, existsEndpointList));

            if (isEndpointListChanged) {
                // 由於輸入資料無法對Endpoint進行資料庫定位(index)
                // 故以Purge再新增的方式完成 --> 且需優先刪除GW_ROUTE 否則違反外鍵關聯
                boolean isGwExistsRoute;
                List<GwRouteEntity> existsGwList = gwRouteEntityRepo.findByApiId(api.getApiId());

                // 避免同一事務中，同一資料表的刪除+更新行為，可能導致的JPA Commit 異常
                // 解決方法：新增事務進行部份處理(必須跨Class)
                apiServiceForClientSubProcess.purgeApiEndpoint(api.getApiId());

                // 新建 Endpoint
                apiEndpointEntityRepo.saveAll(newEndpointList);

                // 如果原本存在Gw Route則進行復歸
                if (CollectionUtils.isNotEmpty(newEndpointList)) {
                    // Rebuild GW Route
                    List<ApiDpyEntity> deploymentList = apiDpyEntityRepo.findByApiId(api.getApiId());
                    List<String> deployFabList = deploymentList.stream()
                            .map(ApiDpyEntity::getFabId)
                            .collect(Collectors.toList());

                    procedureSuccess &= this.deployOrUpdateGwApiByFabList(deployFabList, api.getApiId(), newEndpointList);

                    // 4.4.0 Rebuild Gw API Plugin
                    List<ApiGwPluginDpyEntity> apiPluginDpyList = apiGwPluginDpyEntityRepo.findByApiId(api.getApiId());
                    if (CollectionUtils.isNotEmpty(apiPluginDpyList)) {
                        apiPluginDpyList.forEach(
                                cApiPluginDpy ->
                                {
                                    try {
                                        this.deployOrUpdatePluginOnGateway(cApiPluginDpy);
                                    } catch (Exception e) {
                                        log.error("Api Plugin Fail!! ");
                                        throw new RuntimeException(e);
                                    }
                                }
                        );
                    }
                }
            }
        }

        return procedureSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean createOrUpdateApiDeployment(ApiDpyEntity inDto) throws GatewayControllerException, DataSourceAccessException {
        boolean procedureSuccess = true;

        // 部署資料更新
        Optional<ApiDpyEntity> inSearchApiDeployment = apiDpyEntityRepo.findByFabIdAndApiId(inDto.getFabId(), inDto.getApiId());
        ApiDpyEntity localApiDeployObject;
        if (inSearchApiDeployment.isPresent()) {
            localApiDeployObject = inSearchApiDeployment.get();
            BeanUtils.copyNonNullProperties(inDto, localApiDeployObject);
        } else {
            localApiDeployObject = inDto;
        }
        apiDpyEntityRepo.save(localApiDeployObject);

        // Endpoint Data
        List<ApiEndpointEntity> endpointList = apiEndpointEntityRepo.findByApiId(localApiDeployObject.getApiId());
        if (CollectionUtils.isNotEmpty(endpointList)) {

            procedureSuccess = this.deployOrUpdateGwApiByFabList(Collections.singletonList(localApiDeployObject.getFabId()),
                    localApiDeployObject.getApiId(), endpointList);
        }

        return procedureSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    private boolean deployOrUpdateGwApiByFabList(List<String> newDeployFabList, String apiId, List<ApiEndpointEntity> endpointList) throws GatewayControllerException, DataSourceAccessException {
        boolean procedureSuccess = true;
        // Before lively update gateway info, endpoint data need to be flushed, unless the reference of GwRouteEntity won't catch up the endpoint_id
        Optional<ApiNameDto> inSearchApiName = apiEntityRepo.findNameByApiId(apiId);
        if (CollectionUtils.isNotEmpty(newDeployFabList) && inSearchApiName.isPresent()) {
            ApiNameDto onDeployApiName = inSearchApiName.get();
            for (String deployFabId : newDeployFabList) {
                Optional<GwUpstreamEntity> inSearchGwUpstream = gwUpstreamEntityRepo.findByFabIdAndApiId(deployFabId, apiId);
                if (inSearchGwUpstream.isPresent()) {
                    GwUpstreamEntity onMountGwUpstreamEntity = inSearchGwUpstream.get();
                    for (ApiEndpointEntity endpoint : endpointList) {
                        GwRouteEntity onDeployRoute;
                        Optional<GwRouteEntity> inSearchGwRoute = gwRouteEntityRepo.findByFabIdAndEndpointId(deployFabId, endpoint.getEndpointId());
                        if (inSearchGwRoute.isPresent()) {
                            // Update Procedure
                            onDeployRoute = inSearchGwRoute.get();
                            procedureSuccess = gatewayControlHelper.updateGwRoute(deployFabId, onDeployRoute.getGwRouteId(), onDeployApiName.getSystemName(), endpoint);

                        } else {
                            // Deploy Procedure
                            onDeployRoute = GwRouteEntity.builder()
                                    .gwRouteId(RandomStringUtils.random(GeneralSetting.GW_ROUTE_ID_LENGTH,
                                            GeneralSetting.GW_ROUTE_ID_CONTAIN_CHAR, GeneralSetting.GW_ROUTE_ID_CONTAIN_NUMBER))
                                    .fabId(deployFabId)
                                    .endpointId(endpoint.getEndpointId())
                                    .build();

                            // Call Single Gw service
                            procedureSuccess &= gatewayControlHelper.createGwRoute(deployFabId, onMountGwUpstreamEntity.getGwUpstreamId(), onDeployRoute.getGwRouteId(), onDeployApiName, endpoint);
                            procedureSuccess &= this.initialGwRouteUsagePermission(deployFabId, apiId, onDeployRoute.getGwRouteId());
                        }
                        // Real Data don't sync.
                        gwRouteEntityRepo.save(onDeployRoute);

                        // module Plugin for Api
                        List<ApiGwPluginDpyEntity> apiPluginList = apiGwPluginDpyEntityRepo.findByApiIdAndFabId(apiId, deployFabId);
                        if (CollectionUtils.isNotEmpty(apiPluginList)) {
                            for (ApiGwPluginDpyEntity cApiPluginDpy : apiPluginList) {
                                procedureSuccess &= this.deployOrUpdatePluginOnGateway(cApiPluginDpy);
                            }
                        }
                    }
                }
            }
        }
        return procedureSuccess;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean initialGwRouteUsagePermission(String fabId, String apiId, String gwRouteId) throws GatewayControllerException, DataSourceAccessException {
        boolean gatewaySuccess = true;
        // 查清單
        List<GwApikeyNameDto> onInitialNameList = gwApikeyEntityRepo.findNameListOfExistsApikeyByFabIdAndApiId(fabId, apiId);
        // 組建Gw WhiteList
        gatewaySuccess &= gatewayControlHelper.updateGwRoutePermissionBatch(fabId, gwRouteId, onInitialNameList);
        return gatewaySuccess;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean deleteApiDeployment(ApiDpyEntity inDto) throws GatewayControllerException {
        boolean procedureSuccess = true;

        // 移除指定部署
        Optional<ApiDpyEntity> inSearchApiDeploy = apiDpyEntityRepo.findByFabIdAndApiId(inDto.getFabId(), inDto.getApiId());
        if (inSearchApiDeploy.isPresent()) {
            ApiDpyEntity localApiDeploy = inSearchApiDeploy.get();
            List<ApiEndpointEntity> endpointList = apiEndpointEntityRepo.findByApiId(localApiDeploy.getApiId());

            procedureSuccess = deleteApiGwDeployment(localApiDeploy, endpointList);
            // Client端 部署資料刪除
            apiDpyEntityRepo.delete(localApiDeploy);
        }

        // 檢查當前控制FAB區域是否有其他Deployment資料
        // 若沒有，則清除Client端資料 Endpoint資料
        // ApiEntity 因權限主表限制，不進行刪除
        List<ApiDpyEntity> apiDeployList = apiDpyEntityRepo.findByApiId(inDto.getApiId());
        if (CollectionUtils.isEmpty(apiDeployList)) {
            log.info("Client side has no deploy data, start purge procedure.");
            apiEndpointEntityRepo.deleteByApiId(inDto.getApiId());
        }
        return procedureSuccess;
    }


    @Transactional(rollbackFor = Exception.class)
    private boolean deleteApiGwDeployment(ApiDpyEntity onDeleteDeployment, List<ApiEndpointEntity> endpointList) throws GatewayControllerException {
        boolean procedureSuccess = true;
        for (ApiEndpointEntity endpoint : endpointList) {
            Optional<GwRouteEntity> inSearchGwRoute = gwRouteEntityRepo.findByFabIdAndEndpointId(onDeleteDeployment.getFabId(), endpoint.getEndpointId());
            if (inSearchGwRoute.isPresent()) {
                GwRouteEntity onDeleteGwRoute = inSearchGwRoute.get();
                // Call Single Gw service
                procedureSuccess = gatewayControlHelper.deleteGwRoute(onDeleteDeployment.getFabId(), onDeleteGwRoute.getGwRouteId());
                // Real Data don't sync.
                gwRouteEntityRepo.deleteByFabIdAndEndpointId(onDeleteDeployment.getFabId(), endpoint.getEndpointId());
            }
        }
        return procedureSuccess;
    }

    public void tryDeleteApiBroadcastFromHost(String apiId) {
        try {
            Optional<ApiEntity> onSearchApi = apiEntityRepo.findByApiId(apiId);
            if (onSearchApi.isPresent()) {
                ApiEntity onDeleteApi = onSearchApi.get();
                apiEntityRepo.delete(onDeleteApi);
            }
        } catch (Exception e) {
            log.error("Error deleting ApiEntity with apiId {}: {}", apiId, e.getMessage(), e);
        }
    }

    public void tryCreateOrUpdateApiPluginBroadcastFromHost(ApiGwPluginDpyEntity inDto) throws GatewayControllerException {
        try {
            validateService.validateFabIdExists(inDto.getFabId());
        } catch (Exception e) {
            log.info("Receive Api Plugin Deploy But not in controlled fab. Deploy Data: {}", inDto);
        }


        Optional<ApiEntity> onSearchApi = apiEntityRepo.findByApiId(inDto.getApiId());
        if (onSearchApi.isPresent()) {
            Optional<ApiGwPluginDpyEntity> inSearchPlugin = apiGwPluginDpyEntityRepo.findById(ApiGwPluginDpyEntityId.builder().apiId(inDto.getApiId()).fabId(inDto.getFabId()).gwPluginId(inDto.getGwPluginId()).build());
            ApiGwPluginDpyEntity localPluginObject;
            if (inSearchPlugin.isPresent()) {
                localPluginObject = inSearchPlugin.get();
                BeanUtils.copyNonNullProperties(inDto, localPluginObject);
                apiGwPluginDpyEntityRepo.save(localPluginObject);
            } else {
                apiGwPluginDpyEntityRepo.save(inDto);
            }

            // Deploy on Gateway
            deployOrUpdatePluginOnGateway(inDto);
        }
    }

    public boolean initApiPluginDeployOnGateway(ApiGwPluginDpyEntity inDto) throws GatewayControllerException {
        apiGwPluginDpyEntityRepo.save(inDto);
        return this.deployOrUpdatePluginOnGateway(inDto);
    }

    public boolean deployOrUpdatePluginOnGateway(ApiGwPluginDpyEntity inDto) throws GatewayControllerException {
        boolean procedureSuccess = true;
        Optional<GwPluginEntity> onSearchPlugin = gwPluginEntityRepo.findById(inDto.getGwPluginId());
        if (onSearchPlugin.isPresent()) {
            GwPluginEntity onMountPlugin = onSearchPlugin.get();
            List<GwRouteDto> routeList = gwRouteEntityRepo.findDtoByApiIdAndFabId(inDto.getApiId(), inDto.getFabId());
            log.debug("On init gw plugin: {}", onMountPlugin.getGwPluginName());
            log.debug("deployOrUpdatePluginOnGateway routeList: {}", routeList);
            if (CollectionUtils.isNotEmpty(routeList)) {
                log.debug("deployOrUpdatePluginOnGateway routeList: {}", routeList);
                for (GwRouteDto route : routeList) {
                    log.debug("Fab: {}, ApiId: {}, GW_PLUGIN: {}", route.getFabId(), inDto.getApiId(), onMountPlugin);
                    procedureSuccess = gatewayControlHelper.deployOrUpdateGwPartialPlugin(route.getFabId(), route.getGwRouteId(), onMountPlugin, inDto);
                }
            }
        } else {
            procedureSuccess = false;
        }
        return procedureSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean undeployGwPlugin(String apiId, String fabId, String gwPluginId) throws GatewayControllerException {
        boolean procedureSuccess = true;

        List<GwRouteEntity> routeList = gwRouteEntityRepo.findByApiIdAndFabId(apiId, fabId);
        Optional<GwPluginEntity> onSearchGwPlugin = gwPluginEntityRepo.findById(gwPluginId);
        Optional<ApiGwPluginDpyEntity> onSearchApiPluginDpy = apiGwPluginDpyEntityRepo.findById(ApiGwPluginDpyEntityId.builder().apiId(apiId).fabId(fabId).gwPluginId(gwPluginId).build());
        if (CollectionUtils.isNotEmpty(routeList) &&
                onSearchGwPlugin.isPresent() &&
                onSearchApiPluginDpy.isPresent()) {
            GwPluginEntity onCancelGwPlugin = onSearchGwPlugin.get();
            ApiGwPluginDpyEntity undeployPluginData = onSearchApiPluginDpy.get();
            for (GwRouteEntity route : routeList) {
                procedureSuccess = gatewayControlHelper.undeployGwPlugin(route.getFabId(), route.getGwRouteId(), onCancelGwPlugin, undeployPluginData);
            }
            apiGwPluginDpyEntityRepo.delete(undeployPluginData);
        }
        return procedureSuccess;
    }
}
