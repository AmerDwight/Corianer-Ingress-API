package tw.amer.cia.core.component.structural.init;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.component.functional.coriander.CallHostApiComponent;
import tw.amer.cia.core.component.functional.gateway.GatewayControlHelper;
import tw.amer.cia.core.component.structural.annotation.ClientComponent;
import tw.amer.cia.core.component.structural.property.CoreProperties;
import tw.amer.cia.core.component.structural.resource.sqlCommandLoader.SqlCommander;
import tw.amer.cia.core.model.database.*;
import tw.amer.cia.core.model.pojo.component.property.ClientDeployPropertyFormat;
import tw.amer.cia.core.model.pojo.service.common.AllProxyDataDto;
import tw.amer.cia.core.model.pojo.service.common.api.CompleteApiDto;
import tw.amer.cia.core.model.pojo.service.common.api.CreateOrUpdateApiEndpointDto;
import tw.amer.cia.core.model.pojo.service.common.apikey.CompleteApikeyDto;
import tw.amer.cia.core.model.pojo.service.common.role.CompleteRoleDto;
import tw.amer.cia.core.model.pojo.service.common.system.CompleteSystemDto;
import tw.amer.cia.core.service.client.*;
import tw.amer.cia.core.service.core.GeneralService;
import tw.amer.cia.core.service.database.ExternalSystemConfigEntityService;
import tw.amer.cia.core.service.database.FabEntityService;
import tw.amer.cia.core.service.database.GwPluginEntityService;
import tw.amer.cia.core.service.database.RoleAuthorityEntityService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@ClientComponent
@ConditionalOnProperty(name = "coriander-ingress-api.setting.initial.initial", havingValue = "true")
public class ClientInitializer implements ApplicationRunner {
    private final ConfigurableApplicationContext context;
    @Autowired
    CoreProperties coreProperties;
    @Autowired
    GeneralService generalService;
    @Autowired
    CallHostApiComponent callHostApiComponent;
    @Autowired
    GatewayControlHelper gatewayControlHelper;
    @Autowired
    SqlCommander sqlCommander;
    @Autowired
    FabEntityService cFabFabEntityService;
    @Autowired
    SystemServiceForClient systemServiceForClient;
    @Autowired
    ApiEntityServiceForClient apiEntityServiceForClient;
    @Autowired
    GwPluginEntityService cGwPluginEntityService;
    @Autowired
    RoleServiceForClient roleServiceForClient;
    @Autowired
    RoleDeviceServiceForClient roleDeviceServiceForClient;
    @Autowired
    RoleAuthorityEntityService cRoleAuthorityEntityService;
    @Autowired
    ApikeyServiceForClient apikeyServiceForClient;
    @Autowired
    ProxyServiceForClient proxyServiceForClient;

    @Autowired
    ExternalSystemConfigEntityService cExtCtlService;

    public ClientInitializer(ConfigurableApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // 定義初始化步驟：
        //  1. 確認Host服務存在
        //  2. 確認API Gateway環境
        //     2.0 確認 API Gateway 服務存在
        //     2.1 清理 API Gateway 設定，以防多次重載
        //  3. 取得數據資訊並同時 Inject API Gateway設定
        //     3.0 module Datasource
        //     3.1 取得FAB
        //     3.2 取得系統資料
        //     3.3 取得微服務資料
        //     3.4 取得權限資料
        //     3.5 取得Apikey資料
        //     3.6 取得Proxy資料
        //     3.7 設置Gw的全域設定
        //     3.8 取得Ext Entity資料


        log.info(GeneralSetting.SEPARATE_LINE_DOUBLE);
        log.info("Start Initializing : {}", StringUtils.defaultString(generalService.getDeployClientName()));
        log.info("Version : {}", generalService.getVersionString());
        //  1. 確認Host服務存在
        log.info(GeneralSetting.SEPARATE_LINE);
        log.info("1. Start checking Host is alive. ");
        ifNotSuccessThenShutdownClient(callHostApiComponent.checkHostAlive());


        //  2. 確認API Gateway服務存在
        log.info(GeneralSetting.SEPARATE_LINE);
        log.info("2. Start checking ApiGateway is alive. ");
        ifNotSuccessThenShutdownClient(gatewayControlHelper.checkAllGatewayAlive());


        //  2.1 清空API Gateway資訊
        log.info(GeneralSetting.SEPARATE_LINE);
        log.info("2.1 Start cleaning ApiGateway data. ");
        ifNotSuccessThenShutdownClient(gatewayControlHelper.cleanUpGatewayInfo());


        // 3. Initialization
        log.info(GeneralSetting.SEPARATE_LINE);
        log.info("3. Start initializing data. ");
        // Data Preparation
        List<ClientDeployPropertyFormat> dpyList = coreProperties.getClient().getDeploy();
        Set<String> fabSet = dpyList.stream()
                .flatMap(object -> object.getFab().stream())
                .collect(Collectors.toSet());


        // 3.0 module Database Setting
        log.info(GeneralSetting.SEPARATE_LINE);
        log.info("3.0 Start initializing database setup.");
        ifNotSuccessThenShutdownClient(sqlCommander.initial());


        // 3.1 Fab Data
        log.info(GeneralSetting.SEPARATE_LINE);
        log.info("3.1 Start initializing Fab data. ");
        ifNotSuccessThenShutdownClient(initFabData(fabSet));


        // 3.2 SystemEntity Data
        log.info(GeneralSetting.SEPARATE_LINE);
        log.info("3.2 Start initializing SystemEntity data. ");
        ifNotSuccessThenShutdownClient(initDeployedSystemData(fabSet));


        // 3.3 API Data
        log.info(GeneralSetting.SEPARATE_LINE);
        log.info("3.3 Start initializing Api data. ");
        log.info("Start from module GwPlugin...");
        ifNotSuccessThenShutdownClient(initGwPluginData());

        // 3.3.a Api Data
        log.info("module Api...");
        ifNotSuccessThenShutdownClient(initDeployedApiData(fabSet));


        // 3.4 Role Data
        log.info(GeneralSetting.SEPARATE_LINE);
        log.info("3.4 Start initializing Role data. ");
        ifNotSuccessThenShutdownClient(initDeployedRoleData(fabSet));


        // 3.5 Apikey Data
        log.info(GeneralSetting.SEPARATE_LINE);
        log.info("3.5 Start initializing Apikey data. ");
        ifNotSuccessThenShutdownClient(initDeployedApikeyData(fabSet));


        // 3.6 Proxy Data
        log.info(GeneralSetting.SEPARATE_LINE);
        log.info("3.6 Start initializing Proxy data. ");
        ifNotSuccessThenShutdownClient(initDeployedProxyData());

        // 3.7 module Gateway Global Setting
        log.info(GeneralSetting.SEPARATE_LINE);
        log.info("3.7 module Gateway Global Setting. ");
        ifNotSuccessThenShutdownClient(gatewayControlHelper.setupAllGatewayGlobalRules());

        // 3.8 Ext Entity Data
        log.info(GeneralSetting.SEPARATE_LINE);
        log.info("3.8 Start initializing Ext Entity data. ");
        ifNotSuccessThenShutdownClient(initExtEntityData());

        log.info("Initial Success.");
        log.info(GeneralSetting.SEPARATE_LINE_DOUBLE);
    }

    private void ifNotSuccessThenShutdownClient(boolean necessarySuccess) {
        if (!necessarySuccess) {
            log.error("Calling client showdown... ");
            context.close();
            System.exit(1);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    private boolean initFabData(Set<String> fabSet) {
        boolean procedureSuccess = true;
        try {
            List<FabEntity> fabList = callHostApiComponent.obtainFabDataByFabCollection(fabSet);
            if (CollectionUtils.isNotEmpty(fabList)) {
                for (FabEntity fab : fabList) {
                    cFabFabEntityService.createFab(fab);
                }
            } else {
                log.info("No Fab Detected...");
                procedureSuccess = false;
            }

        } catch (Exception e) {
            log.error("Get Exception： {}", e.getMessage());
            procedureSuccess = false;
        }
        return procedureSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    private boolean initDeployedSystemData(Collection<String> fabCollection) {
        boolean procedureSuccess = true;
        try {
            List<CompleteSystemDto> completeSystemList = callHostApiComponent.obtainCompleteSystemDataByFabCollection(fabCollection);
            if (CollectionUtils.isNotEmpty(completeSystemList)) {
                completeSystemList.forEach(
                        completeSystemDto ->
                        {
                            systemServiceForClient.createOrUpdateSystem(completeSystemDto.getSystem());
                            completeSystemDto.getDeployList().forEach(
                                    cSysDpy ->
                                    {
                                        try {
                                            systemServiceForClient.createOrUpdateSystemDeployment(cSysDpy);
                                        } catch (Exception e) {
                                            log.error("While initializing SystemEntity deployment encounter problem: " +
                                                    "system id = {}, Issue: {}", cSysDpy.getSystemId(), e.getMessage());
                                            throw new RuntimeException(e.getMessage());
                                        }
                                    }
                            );
                        }
                );
            }
        } catch (Exception e) {
            log.error("Get Exception： {}", e.getMessage());
            procedureSuccess = false;
        }
        return procedureSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    private boolean initGwPluginData() {
        boolean procedureSuccess = true;
        try {
            List<GwPluginEntity> gwPluginList = callHostApiComponent.obtainAllGwPluginData();
            if (CollectionUtils.isNotEmpty(gwPluginList)) {
                for (GwPluginEntity gwPlugin : gwPluginList) {
                    cGwPluginEntityService.createOrUpdateGwPlugin(gwPlugin);
                }
            }
        } catch (Exception e) {
            log.error("Get Exception： {}", e.getMessage());
            procedureSuccess = false;
        }
        return procedureSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    private boolean initDeployedApiData(Collection<String> fabCollection) {
        boolean procedureSuccess = true;
        try {
            List<CompleteApiDto> completeApiList = callHostApiComponent.obtainCompleteApiDataByFabCollection(fabCollection);
            if (CollectionUtils.isNotEmpty(completeApiList)) {
                completeApiList.forEach(
                        completeApiDto ->
                        {
                            try {
                                apiEntityServiceForClient.createOrUpdateApi(completeApiDto.getApiEntity());
                                apiEntityServiceForClient.createOrUpdateApiEndpoint(
                                        CreateOrUpdateApiEndpointDto.builder()
                                                .apiEntity(completeApiDto.getApiEntity())
                                                .apiEndpointEntities(completeApiDto.getEndpointList())
                                                .build()
                                );
                                if (CollectionUtils.isNotEmpty(completeApiDto.getDeployList())) {
                                    completeApiDto.getDeployList().forEach(
                                            apiDpyEntity ->
                                            {
                                                try {
                                                    apiEntityServiceForClient.createOrUpdateApiDeployment(apiDpyEntity);
                                                } catch (Exception e) {
                                                    log.error("Initial Api Deployment Fail!! ");
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                    );
                                }
                                if (CollectionUtils.isNotEmpty(completeApiDto.getPluginDpyList())) {
                                    // Api Plugin Dpy 存在單一微服務多廠區配置狀態
                                    completeApiDto.getPluginDpyList().forEach(
                                            apiGwPluginDpyEntity ->
                                            {
                                                try {
                                                    apiEntityServiceForClient.initApiPluginDeployOnGateway(apiGwPluginDpyEntity);
                                                } catch (Exception e) {
                                                    log.error("Initial Api Plugin Fail!! ");
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                    );
                                }

                            } catch (Exception e) {
                                log.error("While initializing Api encounter problem: " +
                                        "Api id = {}, Issue: {}", completeApiDto.getApiEntity().getApiId(), e.getMessage());
                                throw new RuntimeException(e.getMessage());
                            }
                        }
                );
            }
        } catch (Exception e) {
            log.error("Get Exception： {}", e.getMessage());
            procedureSuccess = false;
        }
        return procedureSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    private boolean initDeployedRoleData(Collection<String> fabCollection) {
        boolean procedureSuccess = true;
        try {
            List<CompleteRoleDto> completeRoleList = callHostApiComponent.obtainCompleteRoleDataByFabCollection(fabCollection);
            if (CollectionUtils.isNotEmpty(completeRoleList)) {
                completeRoleList.forEach(
                        completeRoleDto ->
                        {
                            try {
                                roleServiceForClient.createOrUpdateRole(completeRoleDto.getRole());
                                if (CollectionUtils.isNotEmpty(completeRoleDto.getAuthorityList())) {
                                    completeRoleDto.getAuthorityList().forEach(
                                            cRoleAuthority ->
                                            {
                                                cRoleAuthorityEntityService.createRoleAuthority(cRoleAuthority);
                                            }
                                    );
                                }
                            } catch (Exception e) {
                                log.error("While initializing Role data encounter problem: " +
                                        "role id = {}, Issue: {}", completeRoleDto.getRole().getRoleId(), e.getMessage());
                                throw new RuntimeException(e);
                            }
                        }
                );
            }
            // 接下來處理 RoleDevice
            List<RoleDeviceEntity> totalRoleDevice = completeRoleList.stream().flatMap(
                            role -> Optional.ofNullable(role.getDeviceList())
                                    .orElseGet(ArrayList::new)
                                    .stream()
                    ).collect(Collectors.toList())
                    .stream()
                    .filter(cRoleDevice -> StringUtils.equalsIgnoreCase(cRoleDevice.getIsActive(), GeneralSetting.GENERAL_POSITIVE_STRING))
                    .collect(Collectors.toList());
            Set<String> roleIdSetInDeviceList = totalRoleDevice.stream().map(RoleDeviceEntity::getRoleId).collect(Collectors.toSet());
            List<RoleAuthorityEntity> roleAuthorityForDeviceList = completeRoleList.stream().flatMap(
                            role -> Optional
                                    .ofNullable(role.getAuthorityList())
                                    .orElseGet(ArrayList::new)
                                    .stream()
                    ).collect(Collectors.toList())
                    .stream()
                    .filter(authority -> roleIdSetInDeviceList.contains(authority.getRoleId())).collect(Collectors.toList());

            procedureSuccess = roleDeviceServiceForClient.initClientRoleDeviceDeploy(totalRoleDevice, roleAuthorityForDeviceList);


        } catch (Exception e) {
            log.error("Get Exception： {}", e.getMessage());
            procedureSuccess = false;
        }
        return procedureSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    private boolean initDeployedApikeyData(Collection<String> fabCollection) {
        boolean procedureSuccess = true;
        try {
            List<CompleteApikeyDto> completeApikeyList = callHostApiComponent.obtainCompleteApikeyDataByFabCollection(fabCollection);
            if (CollectionUtils.isNotEmpty(completeApikeyList)) {
                completeApikeyList.forEach(
                        completeApikeyDto ->
                        {
                            try {
                                apikeyServiceForClient.createOrUpdateApikeyBySiteFromHost(completeApikeyDto.getApikey());
                                if (CollectionUtils.isNotEmpty(completeApikeyDto.getPermissionList())) {
                                    apikeyServiceForClient.createOrUpdateApikeyPermissionFromHostBatch(completeApikeyDto.getPermissionList());
                                }
                            } catch (Exception e) {
                                log.error("While initializing Role data encounter problem: " +
                                        "apikey id = {}, Issue: {}", completeApikeyDto.getApikey().getApikeyId(), e.getMessage());
                                throw new RuntimeException(e);
                            }
                        }
                );
            }
        } catch (Exception e) {
            log.error("Get Exception： {}", e.getMessage());
            procedureSuccess = false;
        }
        return procedureSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    private boolean initDeployedProxyData() {
        boolean procedureSuccess = true;
        try {
            AllProxyDataDto allProxyDataDto = callHostApiComponent.obtainAllProxyData();
            if (CollectionUtils.isNotEmpty(allProxyDataDto.getProxyList())) {
                proxyServiceForClient.createProxyBatch(allProxyDataDto.getProxyList());
            }
            if (CollectionUtils.isNotEmpty(allProxyDataDto.getNetRefList())) {
                proxyServiceForClient.createProxyNetReferenceBatch(allProxyDataDto.getNetRefList());
            }
            if (CollectionUtils.isNotEmpty(allProxyDataDto.getFabProxyList())) {
                proxyServiceForClient.buildProxyByReference(allProxyDataDto.getFabProxyList());
            }
        } catch (Exception e) {
            log.error("Get Exception： {}", e.getMessage());
            procedureSuccess = false;
        }
        return procedureSuccess;
    }

    @Transactional(rollbackFor = Exception.class)
    private boolean initExtEntityData() {
        boolean procedureSuccess = true;
        try {
            List<ExternalSystemConfigEntity> extControlAllowList = callHostApiComponent.obtainAllExtCtlEntity();
            if (CollectionUtils.isNotEmpty(extControlAllowList)) {
                cExtCtlService.create(extControlAllowList);
            }
        } catch (Exception e) {
            log.error("Get Exception： {}", e.getMessage());
            procedureSuccess = false;
        }
        return procedureSuccess;
    }
}
