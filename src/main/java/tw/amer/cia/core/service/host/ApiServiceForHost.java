package tw.amer.cia.core.service.host;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.component.functional.coriander.CallClientApiComponent;
import tw.amer.cia.core.component.functional.gateway.plugin.GatewayPluginTemplate;
import tw.amer.cia.core.component.functional.gateway.plugin.GatewayPluginTemplateLoader;
import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.component.structural.property.CoreProperties;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.*;
import tw.amer.cia.core.model.database.compositeId.ApiGwPluginDpyEntityId;
import tw.amer.cia.core.model.database.dao.*;
import tw.amer.cia.core.model.pojo.service.common.api.*;
import tw.amer.cia.core.model.pojo.service.common.gateway.CreateOrUpdateGwPluginDto;
import tw.amer.cia.core.model.pojo.service.host.web.manager.apiManagement.Web_ApiDeployedBySiteDto;
import tw.amer.cia.core.model.pojo.service.host.web.manager.apiManagement.Web_ApiEndpointDto;
import tw.amer.cia.core.service.core.ValidateService;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@HostService
public class ApiServiceForHost {
    @Autowired
    CoreProperties coreProperties;
    @Autowired
    ValidateService validateService;
    @Autowired
    CallClientApiComponent callClientApiComponent;
    @Autowired
    ApikeyServiceForHost apikeyServiceForHost;
    @Autowired
    ApiEntityRepo apiEntityRepo;
    @Autowired
    ApiEndpointEntityRepo apiEndpointEntityRepo;
    @Autowired
    ApiDpyEntityRepo apiDpyEntityRepo;
    @Autowired
    GwApikeyPermissionRepo gwApikeyPermissionRepo;
    @Autowired
    RoleAuthorityEntityRepo roleAuthorityEntityRepo;
    @Autowired
    GwPluginEntityRepo gwPluginEntityRepo;
    @Autowired
    ApiGwPluginDpyEntityRepo apiGwPluginDpyEntityRepo;
    @Autowired
    GatewayPluginTemplateLoader gwPluginLoader;

    @Transactional(rollbackFor = {Exception.class})
    public boolean createApi(CreateApiHostDto dto) throws DataSourceAccessException, CiaProcessorException {

        // Data Check
        boolean illegalInput = StringUtils.isEmpty(dto.getSystemName()) || StringUtils.isEmpty(dto.getApiName());
        if (illegalInput) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_API_NAME_INCORRECT_INPUT.getCompleteMessage());
        }
        if (CollectionUtils.isNotEmpty(dto.getDeployment())) {
            dto.getDeployment().forEach(ApiEndpointDto ->
            {
                try {
                    validateService.validateFabIdExists(ApiEndpointDto.getFabId());
                } catch (DataSourceAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        validateService.validateApiNameDuplicate(dto.getSystemName(), dto.getApiName());
        SystemEntity onMountSystem = validateService.validateSystemName(dto.getSystemName());
        List<String> newDeployFabList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dto.getDeployment())) {
            dto.getDeployment().forEach(ApiEndpointDto ->
            {
                try {
                    validateService.validateFabIdExists(ApiEndpointDto.getFabId());
                } catch (DataSourceAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            if (CollectionUtils.isNotEmpty(dto.getDeployment())) {
                newDeployFabList = dto.getDeployment().stream()
                        .map(ApiDeployedFabDto::getFabId)
                        .collect(Collectors.toList());
            }
            validateService.validateSystemDeployBySystemNameAndFabList(dto.getSystemName(), newDeployFabList);
        }


        if (onMountSystem != null) {
            // 資料驗證結束

            // Action-1 微服務主要資訊紀錄
            boolean actionSuccess = true;
            ApiEntity newApi = ApiEntity.builder()
                    .apiId(RandomStringUtils.random(GeneralSetting.API_ID_LENGTH,
                            GeneralSetting.API_ID_CONTAIN_CHAR, GeneralSetting.API_ID_CONTAIN_NUMBER))
                    .systemId(onMountSystem.getSystemId())
                    .apiName(dto.getApiName())
                    .activeStatus("ACTIVE")
                    .createTime(Instant.now())
                    .applicableFlag("Y")
                    .build();
            BeanUtils.copyNonNullProperties(dto, newApi);
            apiEntityRepo.save(newApi);

            // Action-2 微服務端口資訊紀錄： 端口資訊不等於部署資訊，端口資訊僅代表微服務的使用入口定義
            if (CollectionUtils.isNotEmpty(dto.getEndpoint())) {
                for (ApiEndpointDto newEndpointData : dto.getEndpoint()) {
                    ApiEndpointEntity newEndpoint = ApiEndpointEntity.builder()
                            .endpointId(RandomStringUtils.random(GeneralSetting.API_ENDPOINT_ID_LENGTH,
                                    GeneralSetting.API_ENDPOINT_ID_CONTAIN_CHAR, GeneralSetting.API_ENDPOINT_ID_CONTAIN_NUMBER))
                            .apiId(newApi.getApiId())
                            .build();
                    BeanUtils.copyNonNullProperties(newEndpointData, newEndpoint);
                    apiEndpointEntityRepo.save(newEndpoint);
                }
            }

            // Action-3 微服務部署資訊處理，唯有當部署資訊啟動，才會將 微服務資訊、微服務端口資訊輸出到Site內的Client端
            if (CollectionUtils.isNotEmpty(dto.getDeployment())) {
                actionSuccess = this.manageApiDeployment(newApi, newDeployFabList);
            }
            return actionSuccess;
        } else {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.NOT_FOUND,
                    ErrorConstantLib.SERVICE_API_MOUNT_SYSTEM_NOT_FOUND.getCompleteMessage());
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    private boolean manageApiDeployment(ApiEntity apiEntity, List<String> newDeployFabList) throws DataSourceAccessException, CiaProcessorException {
        boolean procedureSuccess = true;
        // 此階段無任何更新動作
        // Endpoint Data
        List<ApiEndpointEntity> endPointList = apiEndpointEntityRepo.findByApiId(apiEntity.getApiId());

        // Deployments by Fab
        List<ApiDpyEntity> existedList = apiDpyEntityRepo.findByApiId(apiEntity.getApiId());
        List<String> deployedFabList = existedList.stream()
                .map(ApiDpyEntity::getFabId)
                .collect(Collectors.toList());

        // 捕捉差集
        // 1. waitDeployFabList 即將進行部署之FAB清單。
        List<String> waitDeployFabList;
        if (CollectionUtils.isNotEmpty(newDeployFabList)) {
            waitDeployFabList = (List<String>) CollectionUtils.subtract(newDeployFabList, deployedFabList);
        } else {
            waitDeployFabList = new ArrayList<>();
        }

        // 2. waitUndeployFabList 為愈刪除之FAB Deploy清單；即原本有，更新後將消失的部分。
        List<String> waitUndeployFabList;
        if (newDeployFabList != null) {
            waitUndeployFabList = (List<String>) CollectionUtils.subtract(deployedFabList, newDeployFabList);
        } else {
            waitUndeployFabList = deployedFabList;
        }
        // 進行 新部署 的動作
        procedureSuccess &= this.deployOrUpdateApiByFabList(waitDeployFabList, apiEntity, endPointList);

        // 進行Undeploy動作
        procedureSuccess &= this.undeployApiByFabList(waitUndeployFabList, apiEntity);

        return procedureSuccess;

    }

    @Transactional(rollbackFor = {Exception.class})
    private boolean undeployApiByFabList(List<String> undeployFabList, ApiEntity apiEntity) throws CiaProcessorException {
        boolean procedureSuccess = true;

        // 進行Undeploy動作
        if (CollectionUtils.isNotEmpty(undeployFabList)) {
            for (String undeployFabId : undeployFabList) {
                // Search Deploy info
                Optional<ApiDpyEntity> inSearchApiDeploy = apiDpyEntityRepo.findByFabIdAndApiId(undeployFabId, apiEntity.getApiId());
                if (inSearchApiDeploy.isPresent()) {
                    ApiDpyEntity apiDeploy = inSearchApiDeploy.get();

                    // apikey permission purge
                    apikeyServiceForHost.revokeApikeyPermissionWhenApiUndeployByFab(undeployFabId, apiEntity.getApiId());

                    // Call Client Service
                    callClientApiComponent.deleteApiDeployment(undeployFabId, apiDeploy);

                    // 資料紀錄
                    apiDpyEntityRepo.delete(apiDeploy);
                }
            }
        }
        return procedureSuccess;
    }

    @Transactional(rollbackFor = {Exception.class})
    private boolean deployOrUpdateApiByFabList(List<String> newDeployFabList, ApiEntity apiEntity, List<ApiEndpointEntity> endpointList) throws DataSourceAccessException, CiaProcessorException {
        boolean actionSuccess = true;

        if (CollectionUtils.isNotEmpty(newDeployFabList)) {
            Set<String> updatedSitesSet = new HashSet<>();
            for (String deployFabId : newDeployFabList) {
                String site = coreProperties.getClientPropertiesByFab(deployFabId).getSiteName();

                // Api / Api Endpoint 僅需要 By Site 進行更新
                boolean siteHasNotBeUpdated = !(updatedSitesSet.contains(site));
                if (siteHasNotBeUpdated) {
                    // 放入已更新記錄
                    updatedSitesSet.add(site);

                    // 呼叫對應的 Client 進行部署準備
                    callClientApiComponent.createOrUpdateApi(deployFabId, apiEntity);
                    callClientApiComponent.createOrUpdateApiEndpoint(deployFabId,
                            CreateOrUpdateApiEndpointDto.builder()
                                    .apiEntity(apiEntity)
                                    .apiEndpointEntities(endpointList)
                                    .build());

                }

                // 針對每一個Fab 的部署
                // 先進行HOST 資料儲存
                Optional<ApiDpyEntity> inSearchApiDeployment = apiDpyEntityRepo.findByFabIdAndApiId(deployFabId, apiEntity.getApiId());

                boolean isNewDeployment = !inSearchApiDeployment.isPresent();
                ApiDpyEntity onUpdateDeployment;
                if (isNewDeployment) {
                    onUpdateDeployment = ApiDpyEntity.builder()
                            .fabId(deployFabId)
                            .apiId(apiEntity.getApiId()).build();
                    apiDpyEntityRepo.save(onUpdateDeployment);
                } else {
                    onUpdateDeployment = inSearchApiDeployment.get();
                }

                // 呼叫對應的 Client 進行部署準備
                callClientApiComponent.createOrUpdateApiDeployment(deployFabId, onUpdateDeployment);
            }
        }
        return actionSuccess;
    }

    @Transactional(rollbackFor = {Exception.class})
    public void updateApi(UpdateApiHostDto dto) throws DataSourceAccessException, CiaProcessorException {
        // Data Check
        boolean illegalInput = StringUtils.isEmpty(dto.getSystemName()) || StringUtils.isEmpty(dto.getApiName());
        if (illegalInput) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_API_NAME_INCORRECT_INPUT.getCompleteMessage());
        }
        List<String> newDeployFabList = new ArrayList<>();
        SystemEntity onMountSystem = validateService.validateSystemName(dto.getSystemName());
        if (CollectionUtils.isNotEmpty(dto.getDeployment())) {
            dto.getDeployment().forEach(ApiEndpointDto ->
            {
                try {
                    validateService.validateFabIdExists(ApiEndpointDto.getFabId());
                } catch (DataSourceAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            if (CollectionUtils.isNotEmpty(dto.getDeployment())) {
                newDeployFabList = dto.getDeployment().stream()
                        .map(ApiDeployedFabDto::getFabId)
                        .collect(Collectors.toList());
            }
            validateService.validateSystemDeployBySystemNameAndFabList(dto.getSystemName(), newDeployFabList);
        }

        if (onMountSystem != null) {
            // 資料檢核完畢
            // Action
            // updateApi
            // updateApiEndpoint
            // updateApiDeployment

            // Api update
            ApiEntity onUpdateApi;
            Optional<ApiEntity> inSearchApi = apiEntityRepo.findBySystemNameAndApiName(
                    dto.getSystemName(), dto.getApiName());
            if (inSearchApi.isPresent()) {
                // Update Data
                onUpdateApi = inSearchApi.get();
                BeanUtils.copyNonNullProperties(dto, onUpdateApi);

                // dataSync
                apiEntityRepo.save(onUpdateApi);
                this.updateApiToDeployedClients(onUpdateApi);


                // 微服務 Endpoint update
                // 若無則不進行更新
                if (CollectionUtils.isNotEmpty(dto.getEndpoint())) {
                    // Host 資料儲存
                    // 由於輸入Endpoint資料無法對資料庫進行交叉定位(index)
                    // 故以Purge再新增的方式完成
                    apiEndpointEntityRepo.deleteByApiId(onUpdateApi.getApiId());
                    // 新建 Endpoint
                    List<ApiEndpointEntity> newApiEndpointList = new ArrayList<>();
                    for (ApiEndpointDto onUpdateEndpointData : dto.getEndpoint()) {
                        ApiEndpointEntity newEndpoint = ApiEndpointEntity.builder()
                                .endpointId(RandomStringUtils.random(GeneralSetting.API_ENDPOINT_ID_LENGTH,
                                        GeneralSetting.API_ENDPOINT_ID_CONTAIN_CHAR, GeneralSetting.API_ENDPOINT_ID_CONTAIN_NUMBER))
                                .apiId(onUpdateApi.getApiId())
                                .build();
                        BeanUtils.copyNonNullProperties(onUpdateEndpointData, newEndpoint);
                        apiEndpointEntityRepo.save(newEndpoint);
                        newApiEndpointList.add(newEndpoint);
                    }
                    this.updateApiEndpointToDeployedClients(
                            onUpdateApi, newApiEndpointList);
                } else if (dto.getEndpoint() != null) {
                    // 微服務 Endpoint 不為空，則代表用戶需要清掉所有的Endpoint
                    apiEndpointEntityRepo.deleteByApiId(onUpdateApi.getApiId());
                    this.updateApiEndpointToDeployedClients(
                            onUpdateApi, new ArrayList<>());
                }
                // Deployment update
                // 若無則不進行更新
                if (CollectionUtils.isNotEmpty(dto.getDeployment())) {
                    this.manageApiDeployment(onUpdateApi, newDeployFabList);
                } else if (dto.getDeployment() != null) {
                    // 微服務 Deployment 不為空，則代表用戶需要清掉所有的Endpoint
                    this.manageApiDeployment(onUpdateApi, new ArrayList<>());
                }
            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.NOT_FOUND,
                        ErrorConstantLib.SERVICE_API_NOT_FOUND.getCompleteMessage());
            }
        } else {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.NOT_FOUND,
                    ErrorConstantLib.SERVICE_API_MOUNT_SYSTEM_NOT_FOUND.getCompleteMessage());
        }
    }

    private void updateApiEndpointToDeployedClients(ApiEntity apiEntity, List<ApiEndpointEntity> newApiEndpointList) throws CiaProcessorException {
        List<String> deployFabList = apiDpyEntityRepo.findFabIdByApiId(apiEntity.getApiId());
        if (CollectionUtils.isNotEmpty(deployFabList)) {
            callClientApiComponent.updateApiEndpointToDeployedClients(deployFabList,
                    CreateOrUpdateApiEndpointDto.builder()
                            .apiEntity(apiEntity)
                            .apiEndpointEntities(newApiEndpointList)
                            .build());
        }
    }

    private void updateApiToDeployedClients(ApiEntity onUpdateApi) throws CiaProcessorException {
        List<String> deployFabList = apiDpyEntityRepo.findFabIdByApiId(onUpdateApi.getApiId());
        if (CollectionUtils.isNotEmpty(deployFabList)) {
            callClientApiComponent.updateApiToDeployedClients(deployFabList, onUpdateApi);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public boolean deleteApi(DeleteApiHostDto dto) throws DataSourceAccessException, CiaProcessorException {
        // Data Check
        boolean illegalInput = StringUtils.isEmpty(dto.getSystemName()) || StringUtils.isEmpty(dto.getApiName());
        if (illegalInput) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.SERVICE_API_NAME_INCORRECT_INPUT.getCompleteMessage());
        }
        SystemEntity onMountSystem = validateService.validateSystemName(dto.getSystemName());

        if (onMountSystem != null) {
            // Action
            boolean deleteApiSuccess = true;
            boolean revokeRoleAuthorityAndKeyPermission = true;
            boolean deleteApiEndpointSuccess = true;
            boolean deleteApiDeploymentSuccess = true;

            // Api update
            ApiEntity onDeleteApi;
            Optional<ApiEntity> inSearchApi = apiEntityRepo.findBySystemNameAndApiName(
                    dto.getSystemName(), dto.getApiName());
            if (inSearchApi.isPresent()) {
                // 資料驗證完成
                onDeleteApi = inSearchApi.get();

                // 資料準備
                List<ApiEndpointEntity> endpointList = apiEndpointEntityRepo.findByApiId(onDeleteApi.getApiId());
                List<ApiDpyEntity> depolyList = apiDpyEntityRepo.findByApiId(onDeleteApi.getApiId());
                List<String> depolyFabList = depolyList.stream().map(ApiDpyEntity::getFabId).collect(Collectors.toList());

                // 作業流程
                // 1. Cancel Deployment
                // 2. Delete Api Endpoint
                // 3  Revoke Apikey Permission and Role Authority
                // 4. Delete Api

                // 1. Erase Deployment
                if (CollectionUtils.isNotEmpty(depolyList)) {
                    // Both GwRouteEntity and ApiDpyEntity In Clients will be Erased
                    deleteApiDeploymentSuccess = this.manageApiDeployment(onDeleteApi, null);
                }

                // 2. Erase Endpoint Data
                if (CollectionUtils.isNotEmpty(endpointList)) {
                    apiEndpointEntityRepo.deleteAll(endpointList);
                }

                // 3. Revoke Apikey Permission and Role Authority
                revokeRoleAuthorityAndKeyPermission = this.apiAuthoritiesCascadeRevoke(onDeleteApi.getApiId());

                // 4. Delete Api Plugin Data
                apiGwPluginDpyEntityRepo.deleteAll(apiGwPluginDpyEntityRepo.findByApiId(onDeleteApi.getApiId()));

                // 5. Delete Api
                Map<String, Boolean> broadcastResults = callClientApiComponent.tryDeleteApiBroadcast(onDeleteApi.getApiId());
                if (MapUtils.isNotEmpty(broadcastResults)) {
                    broadcastResults.forEach((url, success) ->
                    {
                        if (success) {
                            log.info("Successfully processed request for URL: {}", url);
                        } else {
                            log.error("Failed to process request for URL: {}", url);
                        }
                    });
                }

                apiEntityRepo.delete(onDeleteApi);

                return deleteApiSuccess &&
                        revokeRoleAuthorityAndKeyPermission &&
                        deleteApiEndpointSuccess &&
                        deleteApiDeploymentSuccess;

            } else {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.NOT_FOUND,
                        ErrorConstantLib.SERVICE_API_NOT_FOUND.getCompleteMessage());
            }
        } else {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.NOT_FOUND,
                    ErrorConstantLib.SERVICE_API_MOUNT_SYSTEM_NOT_FOUND.getCompleteMessage());
        }
    }

    private boolean apiAuthoritiesCascadeRevoke(String apiId) {
        boolean actionSuccess = true;

        // Revoke Apikey Permissions
        gwApikeyPermissionRepo.deleteByApiId(apiId);
        // Revoke Role Authorities
        roleAuthorityEntityRepo.deleteByApiId(apiId);

        return actionSuccess;
    }

    public List<ApiEndpointDto> retrieveApiEndpointInfoClientApi(String apiId) {
        List<ApiEndpointEntity> endpointData = apiEndpointEntityRepo.findByApiId(apiId);
        return endpointData.stream()
                .map(ApiEndpointDto::new)
                .collect(Collectors.toList());
    }

    public List<Web_ApiEndpointDto> webRetrieveApiEndpointInfo(String apiId) {
        List<ApiEndpointEntity> endpointData = apiEndpointEntityRepo.findByApiId(apiId);
        return endpointData.stream()
                .map(Web_ApiEndpointDto::new)
                .collect(Collectors.toList());
    }

    public List<ApiEndpointEntity> retrieveApiEndpointList(String apiId) {
        return apiEndpointEntityRepo.findByApiId(apiId);
    }

    public List<ApiDeployedFabDto> retrieveApiDeploymentInfoClientApi(String apiId) {
        List<ApiDpyEntity> deployData = apiDpyEntityRepo.findByApiId(apiId);
        return deployData.stream()
                .map(ApiDeployedFabDto::new)
                .collect(Collectors.toList());
    }

    public List<ApiGwPluginInfoDto> getAllGwPluginInfo() {
        return gwPluginEntityRepo.getAllGwPluginInfo();
    }

    @Transactional(rollbackFor = {Exception.class})
    public boolean createOrUpdateGwPlugin(CreateOrUpdateGwPluginDto inDto) throws CiaProcessorException {
        boolean procedureSuccess = true;

        GwPluginEntity onModifyPlugin = null;
        if (StringUtils.isNotEmpty(inDto.getGwPluginId())) {
            Optional<GwPluginEntity> onSearchPlugin = gwPluginEntityRepo.findById(inDto.getGwPluginId());
            if (onSearchPlugin.isPresent()) {
                onModifyPlugin = onSearchPlugin.get();
            } else {
                onModifyPlugin = GwPluginEntity.builder()
                        .gwPluginId(inDto.getGwPluginId())
                        .build();
            }
        }
        if (onModifyPlugin == null) {
            onModifyPlugin = GwPluginEntity.builder()
                    .gwPluginId(RandomStringUtils.random(GeneralSetting.GW_PLUGIN_ID_LENGTH,
                            GeneralSetting.GW_PLUGIN_ID_CONTAIN_CHAR, GeneralSetting.GW_PLUGIN_ID_CONTAIN_NUMBER))
                    .build();
        }
        BeanUtils.copyNonNullProperties(inDto, onModifyPlugin);
        gwPluginEntityRepo.save(onModifyPlugin);
        callClientApiComponent.createOrUpdateGwPluginBroadcast(onModifyPlugin);
        return procedureSuccess;
    }

    @Transactional(rollbackFor = {Exception.class})
    public boolean createOrUpdateApiPlugin(DeployApiGwPluginDto inDto) throws CiaProcessorException, DataSourceAccessException {
        boolean procedureSuccess = true;

        GwPluginEntity cGwPlugin = validateService.validateGwPluginId(inDto.getGwPluginId());

        ApiGwPluginDpyEntity onModifyPlugin = null;
        ApiGwPluginDpyEntityId onCreateOrUpdatePluginId = ApiGwPluginDpyEntityId.builder().apiId(inDto.getApiId()).fabId(inDto.getFabId()).gwPluginId(inDto.getGwPluginId()).build();
        Optional<ApiGwPluginDpyEntity> onSearchPlugin = apiGwPluginDpyEntityRepo.findById(onCreateOrUpdatePluginId);
        if (onSearchPlugin.isPresent()) {
            onModifyPlugin = onSearchPlugin.get();
        } else {
            onModifyPlugin = ApiGwPluginDpyEntity.builder()
                    .apiId(inDto.getApiId())
                    .gwPluginId(inDto.getGwPluginId())
                    .build();
        }
        BeanUtils.copyNonNullProperties(inDto, onModifyPlugin);

        // 240924 using pluginTemplate to restore parameters
        if (StringUtils.isNotEmpty(cGwPlugin.getGwPluginTemplate())) {
            GatewayPluginTemplate thisPlugin = gwPluginLoader.getPluginTemplate(cGwPlugin.getGwPluginTemplate());
            if (ObjectUtils.isNotEmpty(thisPlugin)) {
                onModifyPlugin.setGwPluginParameter(
                        thisPlugin.getEncodedParameters(inDto.getGwPluginParameterMap())
                );
            }

        } else {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getCompleteMessage() +
                            " GwPluginEntity - GwPluginTemplate is null."
            );
        }

        apiGwPluginDpyEntityRepo.save(onModifyPlugin);
        callClientApiComponent.createOrUpdateApiPluginBroadcast(onModifyPlugin);
        return procedureSuccess;
    }

    public boolean deleteGwPlugin(String gwPluginId) throws CiaProcessorException {
        boolean procedureSuccess = true;
        List<ApiGwPluginDpyEntity> deployedApiPlugin = apiGwPluginDpyEntityRepo.findByGwPluginId(gwPluginId);
        if (CollectionUtils.isNotEmpty(deployedApiPlugin)) {
            for (ApiGwPluginDpyEntity obj : deployedApiPlugin) {
                this.undeployApiPlugin(obj.getApiId(), obj.getFabId(), obj.getGwPluginId());
            }
        }
        callClientApiComponent.undeployGwPluginBroadcast(gwPluginId);
        return procedureSuccess;
    }

    public void undeployApiPlugin(String apiId, String fabId, String gwPluginId) throws CiaProcessorException {
        Optional<ApiGwPluginDpyEntity> onSearchApiPlugin = apiGwPluginDpyEntityRepo.findById(ApiGwPluginDpyEntityId.builder().apiId(apiId).fabId(fabId).gwPluginId(gwPluginId).build());
        if (onSearchApiPlugin.isPresent()) {
            ApiGwPluginDpyEntity undeployApiPlugin = onSearchApiPlugin.get();
            Optional<ApiDpyEntity> onSearchDeployData = apiDpyEntityRepo.findByApiIdAndFabId(apiId, fabId);

            if (onSearchDeployData.isPresent()) {
                ApiDpyEntity deployedApi = onSearchDeployData.get();
                callClientApiComponent.undeployApiPlugin(deployedApi.getFabId(), apiId, gwPluginId);
            }
            apiGwPluginDpyEntityRepo.delete(undeployApiPlugin);
        } else {
            log.error("Can not find Api: {} relate to Plugin: {} ", apiId, gwPluginId);
        }
    }

    public List<CompleteApiDto> retrieveCompleteApiDataByFabId(Collection<String> fabIdSet) {
        // Build Result
        List<CompleteApiDto> result = new ArrayList<>();

        // Process
        List<ApiDpyEntity> dpyList = apiDpyEntityRepo.findByFabIdIn(fabIdSet);
        if (CollectionUtils.isNotEmpty(dpyList)) {
            Set<String> apiIdSet = dpyList.stream().map(ApiDpyEntity::getApiId).collect(Collectors.toSet());
            apiIdSet.forEach(apiId ->
            {
                Optional<ApiEntity> onSearchApi = apiEntityRepo.findByApiId(apiId);
                if (onSearchApi.isPresent()) {
                    ApiEntity targetApi = onSearchApi.get();
                    List<ApiDpyEntity> targetDpyList = dpyList.stream()
                            .filter(obj -> StringUtils.isNotEmpty(obj.getApiId()) &&
                                    StringUtils.equals(obj.getApiId(), apiId))
                            .collect(Collectors.toList());
                    List<ApiEndpointEntity> targetEndpointList = apiEndpointEntityRepo.findByApiId(apiId);
                    List<ApiGwPluginDpyEntity> targetPluginList = apiGwPluginDpyEntityRepo.findByApiIdAndFabIdIn(apiId, fabIdSet);
                    result.add(CompleteApiDto.builder()
                            .apiEntity(targetApi)
                            .deployList(targetDpyList)
                            .endpointList(targetEndpointList)
                            .pluginDpyList(targetPluginList)
                            .build());
                }
            });
        }
        return result;
    }

    public List<Web_ApiDeployedBySiteDto> retrieveApiDeploymentByApiId(String apiId) throws DataSourceAccessException {
        // Verify
        validateService.validateApiByApiId(apiId);

        // Prepare
        Map<String, List<String>> fabIdsBySiteMap =
                coreProperties.fabIdListTransferTofabIdsBySiteMap(
                        apiDpyEntityRepo.findSystemDeployedFabIdListByApiId(apiId));
        return Web_ApiDeployedBySiteDto
                .importFromAllAvailableFabAndDeployedFab(
                        fabIdsBySiteMap, apiDpyEntityRepo.findFabIdByApiId(apiId));
    }

    public List<Web_ApiDeployedBySiteDto> retrieveApiGwDeploymentById(String apiId, String gwPluginId) throws DataSourceAccessException {
        // Verify
        ApiEntity api = validateService.validateApiByApiId(apiId);
        GwPluginEntity gwPlugin = validateService.validateGwPluginId(gwPluginId);

        // Prepare
        Map<String, List<String>> fabIdsBySiteMap =
                coreProperties.fabIdListTransferTofabIdsBySiteMap(
                        apiDpyEntityRepo.findFabIdByApiId(apiId));
        return Web_ApiDeployedBySiteDto
                .importFromAllAvailableFabAndDeployedFab(
                        fabIdsBySiteMap,
                        apiGwPluginDpyEntityRepo.findByApiIdAndGwPluginId(apiId, gwPluginId).stream()
                                .map(ApiGwPluginDpyEntity::getFabId)
                                .collect(Collectors.toList()));
    }

    public String buildApiGwUri(String apiEngName, String endpointItf) throws DataSourceAccessException {
        if (StringUtils.isBlank(apiEngName) || StringUtils.isBlank(endpointItf)) {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getMessage() + " API_ENG_NAME/ Endpoint Interface "
            );
        }
        StringBuilder sb = new StringBuilder("/");
        sb.append(apiEngName);
        sb.append("/").append(endpointItf);
        return sb.toString();
    }


}
