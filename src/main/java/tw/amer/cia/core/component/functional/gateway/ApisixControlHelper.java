package tw.amer.cia.core.component.functional.gateway;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.common.gateway.GatewayFormatter;
import tw.amer.cia.core.common.utility.JsonStringProcessor;
import tw.amer.cia.core.component.functional.gateway.plugin.GatewayPluginTemplateLoader;
import tw.amer.cia.core.component.structural.httpClient.HttpRequestSender;
import tw.amer.cia.core.component.structural.property.ApisixProperties;
import tw.amer.cia.core.exception.gateway.ApisixProcessorException;
import tw.amer.cia.core.exception.gateway.GatewayControllerException;
import tw.amer.cia.core.model.database.ApiEndpointEntity;
import tw.amer.cia.core.model.database.GwPluginEntity;
import tw.amer.cia.core.model.database.ApiGwPluginDpyEntity;
import tw.amer.cia.core.model.database.SystemDpyEntity;
import tw.amer.cia.core.model.pojo.component.gateway.GwApikeyNameDto;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.ApisixPropertyFormat;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.MetaDto;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.apikey.GwApikeyCreateCommandDto;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.plugin.ApisixGlobalPluginDtoFactory;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.plugin.GlobalPluginMap;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.plugin.HttpLoggerPluginDto;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.plugin.SetGlobalPluginDto;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.GwRouteCreateInfoDto;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.GwRoutePropertyCommandDto;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.GwRouteRetrieveResponseDto;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.GwRouteRetrieveValueTagDto;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.plugin.*;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.upstream.*;
import tw.amer.cia.core.model.pojo.service.common.api.ApiNameDto;
import tw.amer.cia.core.model.pojo.service.common.gateway.ExternalGatewayInfoDto;
import tw.amer.cia.core.model.pojo.service.common.gateway.ExternalGatewayInfoListDto;
import tw.amer.cia.core.service.core.ValidateService;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ApisixControlHelper extends HttpRequestSender implements GatewayControlHelper {

    @Setter
    @Value("${coriander-ingress-api.module.role-device-management:false}")
    private boolean IS_ROLE_DEVICE_MANAGEMENT_ENABLED;

    @Autowired
    ApisixProperties configProperties;

    @Autowired
    GatewayPluginTemplateLoader pluginTemplateLoader;

    @Autowired
    ValidateService validateService;

    @Autowired
    ApisixGlobalPluginDtoFactory apisixGlobalPluginDtoFactory;

    @Autowired
    ApisixRoutePluginDtoFactory apisixRoutePluginDtoFactory;

    // Command Path
    private final String MANAGE_APIKEY_URI = "/apisix/admin/consumers";
    private final String MANAGE_GLOBAL_RULE_URI_WITH_ID = "/apisix/admin/global_rules/{ruleId}";
    private final String MANAGE_APIKEY_URI_WITH_USERNAME = "/apisix/admin/consumers/{username}";
    private final String MANAGE_ROUTE_URI = "/apisix/admin/routes";
    private final String MANAGE_ROUTE_URI_WITH_ID = "/apisix/admin/routes/{id}";
    private final String MANAGE_UPSTREAM_URI_WITH_ID = "/apisix/admin/upstreams/{id}";

    // Retrieve Path
    private final String RETRIEVE_ROUTE_LIST_URI = "/apisix/admin/routes";
    private final String RETRIEVE_CONSUMER_LIST_URI = "/apisix/admin/consumers";
    private final String RETRIEVE_UPSTREAM_LIST_URI = "/apisix/admin/upstreams";
    private final String RETRIEVE_GLOBAL_RULE_LIST_URI = "/apisix/admin/global_rules";

    // General Configs
    private boolean IS_USE_HTTPS;

    public ApisixControlHelper() {
        super();
    }

    @PostConstruct
    public void initApisixControlHelper() {
        this.IS_USE_HTTPS = StringUtils.equalsIgnoreCase("HTTPS", configProperties.getScheme());
    }

    public static List<String> convertHttpMethodStringToList(String httpMethodString) {
        // Define valid HTTP methods in uppercase for comparison
        List<String> validHttpMethods = GeneralSetting.getDefaultHttpMethods();

        // Convert the input to uppercase to handle case-insensitivity
        String dataUpper = httpMethodString.toUpperCase();

        // Check if the input explicitly contains "ALL"
        if (dataUpper.contains("ALL")) {
            return new ArrayList<>(validHttpMethods);
        }

        // Split the string by non-word characters except for hyphen (-)
        String[] items = dataUpper.split("[,\\-/]");

        List<String> resultList = new ArrayList<>();
        for (String item : items) {
            if (validHttpMethods.contains(item)) {
                resultList.add(item);
            }
        }
        return resultList;
    }

    @Override
    public boolean checkAllGatewayAlive() {

        Map<String, ApisixPropertyFormat> apisixDpyMap = configProperties.getDeployMapBySite();

        for (String site : apisixDpyMap.keySet()) {
            boolean isApisixAlive = false;

            ApisixPropertyFormat targetSite = apisixDpyMap.get(site);
            String url = ApisixControlHelper.buildUrl(IS_USE_HTTPS, targetSite.getInternalGatewayHost(), targetSite.getInternalGatewayServicePort(), "");

            HttpEntity<Object> entity = new HttpEntity<>("", gatewayControlHeader(targetSite.getAdminKey(), null));
            // Send GET request
            ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.GET, url);
            List<String> servers = commandResult.getHeaders().get("Server");

            // Check Response Header
            if (CollectionUtils.isNotEmpty(servers)) {
                for (String server : servers) {
                    if (server.startsWith(configProperties.getGatewayType().toUpperCase())) {
                        isApisixAlive = true;
                        log.info("Apisix is found for {}, on {}:{}.", site, targetSite.getInternalGatewayHost(), targetSite.getInternalGatewayServicePort());
                        break;
                    }
                }
            }
            if (!isApisixAlive) {
                log.error("Apisix is not found for {},  {}:{}.", site, targetSite.getInternalGatewayHost(), targetSite.getInternalGatewayServicePort());
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean cleanUpGatewayInfo() throws ApisixProcessorException {
        Map<String, ApisixPropertyFormat> apisixDpyMap = configProperties.getDeployMapBySite();

        for (String site : apisixDpyMap.keySet()) {
            ApisixPropertyFormat targetSite = apisixDpyMap.get(site);

            // 清理 APISIX Route Info
            eliminateDataWithInApisix(
                    targetSite, MANAGE_ROUTE_URI_WITH_ID,
                    retrieveIdListByKey(targetSite, RETRIEVE_ROUTE_LIST_URI, "id"));

            // 清理 APISIX Consumer Info
            eliminateDataWithInApisix(
                    targetSite, MANAGE_APIKEY_URI_WITH_USERNAME,
                    retrieveIdListByKey(targetSite, RETRIEVE_CONSUMER_LIST_URI, "username"));

            // 清理 APISIX Upstream Info
            eliminateDataWithInApisix(
                    targetSite, MANAGE_UPSTREAM_URI_WITH_ID,
                    retrieveIdListByKey(targetSite, RETRIEVE_UPSTREAM_LIST_URI, "id"));

            // 清理 APISIX Global Rule
            eliminateDataWithInApisix(
                    targetSite, MANAGE_GLOBAL_RULE_URI_WITH_ID,
                    retrieveIdListByKey(targetSite, RETRIEVE_GLOBAL_RULE_LIST_URI, "id"));
        }
        return true;
    }

    private void eliminateDataWithInApisix(ApisixPropertyFormat connectionInfo, String apisixAdminDeleteUrl, List<String> onDeleteKeys) throws ApisixProcessorException {
        if (CollectionUtils.isNotEmpty(onDeleteKeys)) {
            for (String onDeleteKey : onDeleteKeys) {
                eliminateDataWithInApisix(connectionInfo, apisixAdminDeleteUrl, onDeleteKey);
            }
        }
    }

    private void eliminateDataWithInApisix(ApisixPropertyFormat connectionInfo, String apisixAdminDeleteUrl, String onDeleteKey) throws ApisixProcessorException {
        String retrieveInfoUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, connectionInfo.getInternalGatewayHost(), connectionInfo.getInternalGatewayAdminPort(),
                UriComponentsBuilder.fromPath(apisixAdminDeleteUrl).buildAndExpand(onDeleteKey).toString());
        HttpEntity<Object> retrieveEntity = new HttpEntity<>(gatewayControlHeader(connectionInfo.getAdminKey(), null));
        ResponseEntity<String> commandResult = sendGatewayHttpCommand(retrieveEntity, HttpMethod.DELETE, retrieveInfoUrl);
    }

    private List<String> retrieveIdListByKey(ApisixPropertyFormat connectionInfo, String apisixAdminRetrieveUrl, String targetKey) throws ApisixProcessorException {
        // Retrieve APISIX Data
        String retrieveInfoUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, connectionInfo.getInternalGatewayHost(), connectionInfo.getInternalGatewayAdminPort(),
                apisixAdminRetrieveUrl);
        HttpEntity<Object> retrieveEntity = new HttpEntity<>(gatewayControlHeader(connectionInfo.getAdminKey(), null));
        ResponseEntity<String> commandResult = sendGatewayHttpCommand(retrieveEntity, HttpMethod.GET, retrieveInfoUrl);

        return extractApisixListKeyValues(commandResult.getBody(), targetKey);
    }

    private List<String> extractApisixListKeyValues(String jsonBodyString, String targetKey) {
        List<String> values = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            // 解析JSON字串
            JsonNode rootNode = mapper.readTree(jsonBodyString);
            // 獲取total值
            int total = rootNode.get("total").asInt();
            // 獲取list陣列
            JsonNode listNode = rootNode.get("list");
            // 遍歷list中的每個項目
            for (JsonNode item : listNode) {
                // 根據JSON結構找到目標鍵值
                JsonNode valueNode = item.get("value");
                if (valueNode != null && valueNode.has(targetKey)) {
                    values.add(valueNode.get(targetKey).asText());
                }
            }
            // 驗證提取的值的數量是否等於total
            if (values.size() != total) {
                throw new RuntimeException("Extracted values count (" + values.size()
                        + ") does not match total (" + total + ")");
            }
            return values;
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract key values: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean setupAllGatewayGlobalRules() throws ApisixProcessorException {
        Map<String, ApisixPropertyFormat> apisixDpyMap = configProperties.getDeployMapBySite();
        for (String site : apisixDpyMap.keySet()) {
            ApisixPropertyFormat targetSite = apisixDpyMap.get(site);
            // Build Setup Plugin Command
            SetGlobalPluginDto setGlobalPluginDto = SetGlobalPluginDto.builder().build();

            // preparing log server data
            HttpLoggerPluginDto httpLoggerPluginDto = apisixGlobalPluginDtoFactory.createHttpLoggerPluginDto(
                    HttpRequestSender.buildUrl(
                            StringUtils.equalsIgnoreCase(targetSite.getLogServerScheme(), "HTTPS"),
                            targetSite.getLogServer(),
                            targetSite.getLogServerPort(),
                            targetSite.getLogServerPath()
                    ),
                    targetSite.getLogServerAuthKey()
            );

            GlobalPluginMap globalPluginMap = GlobalPluginMap.builder().httpLoggerPluginDto(httpLoggerPluginDto).build();
            setGlobalPluginDto.setGlobalPluginMap(globalPluginMap);
            log.info("on-sent Apisix command: {}", JsonStringProcessor.jsonObjectParser(setGlobalPluginDto));

            // Send Command to Apisix
            String url = ApisixControlHelper.buildUrl(IS_USE_HTTPS, targetSite.getInternalGatewayHost(), targetSite.getInternalGatewayAdminPort(),
                    // 指定任意RuleId
                    UriComponentsBuilder.fromPath(MANAGE_GLOBAL_RULE_URI_WITH_ID).buildAndExpand("1").toString());

            HttpEntity<Object> entity = new HttpEntity<>(setGlobalPluginDto, gatewayControlHeader(targetSite.getAdminKey(), null));
            // Send GET request
            ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.PUT, url);
            if (!commandResult.getStatusCode().is2xxSuccessful()) {
                throw ApisixProcessorException.createExceptionForHttp(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        ErrorConstantLib.GATEWAY_COMMAND_ERROR.getCompleteMessage());
            }
        }
        return true;
    }

    @Override
    public boolean createGwApikeyWithFab(String targetFabId, String roleId, String apikeyId, String keyName) throws ApisixProcessorException {
        // Prepare Data
        ApisixPropertyFormat targetFab = configProperties.getPropertiesByFab(targetFabId);
        String url = ApisixControlHelper.buildUrl(IS_USE_HTTPS, targetFab.getInternalGatewayHost(), targetFab.getInternalGatewayAdminPort(), MANAGE_APIKEY_URI);
        GwApikeyCreateCommandDto commandDto = GwApikeyCreateCommandDto.builder()
                .nameInRoleDashKeyName((getGwKeyName(roleId, keyName)))
                .plugins(GwApikeyCreateCommandDto.Plugins.builder()
                        .keyAuth(GwApikeyCreateCommandDto.Plugins.KeyAuth.builder()
                                .key(apikeyId)
                                .build())
                        .build())
                .build();
        HttpEntity<Object> entity = new HttpEntity<>(commandDto, gatewayControlHeader(targetFab.getAdminKey(), null));

        // Send PUT request
        ResponseEntity<String> commandResult = sendGatewayHttpCommand(entity, HttpMethod.PUT, url);

        return commandResult.getStatusCode().is2xxSuccessful();
    }

    @Override
    public void changeApikeyStatus(String fabId, String roleId, String apikeyId, String keyName, boolean isActive) throws ApisixProcessorException {
        // Prepare Data
        ApisixPropertyFormat targetFab = configProperties.getPropertiesByFab(fabId);
        String url = ApisixControlHelper.buildUrl(IS_USE_HTTPS, targetFab.getInternalGatewayHost(), targetFab.getInternalGatewayAdminPort(), MANAGE_APIKEY_URI);
        GwApikeyCreateCommandDto commandDto = GwApikeyCreateCommandDto.builder()
                .nameInRoleDashKeyName((getGwKeyName(roleId, keyName)))
                .plugins(GwApikeyCreateCommandDto.Plugins.builder()
                        .keyAuth(GwApikeyCreateCommandDto.Plugins.KeyAuth.builder()
                                .key(isActive ? apikeyId : GeneralSetting.APIKEY_DISABLED_KEY_STRING)
                                .build())
                        .build())
                .build();
        HttpEntity<Object> entity = new HttpEntity<>(commandDto, gatewayControlHeader(targetFab.getAdminKey(), null));

        // Send PUT request
        ResponseEntity<String> commandResult = sendGatewayHttpCommand(entity, HttpMethod.PUT, url);
    }

    @Override
    public boolean createGwApikeyBySite(String roleId, String apikeyId, String keyName) throws ApisixProcessorException {
        boolean procedureSuccess = true;

        // Prepare Data
        if (MapUtils.isNotEmpty(configProperties.getDeployMapBySite())) {
            for (ApisixPropertyFormat apisixDeploy : configProperties.getDeployMapBySite().values()) {
                String url = ApisixControlHelper.buildUrl(IS_USE_HTTPS, apisixDeploy.getInternalGatewayHost(), apisixDeploy.getInternalGatewayAdminPort(), MANAGE_APIKEY_URI);
                GwApikeyCreateCommandDto commandDto = GwApikeyCreateCommandDto.builder()
                        .nameInRoleDashKeyName((getGwKeyName(roleId, keyName)))
                        .plugins(GwApikeyCreateCommandDto.Plugins.builder()
                                .keyAuth(GwApikeyCreateCommandDto.Plugins.KeyAuth.builder()
                                        .key(apikeyId)
                                        .build())
                                .build())
                        .build();
                HttpEntity<Object> entity = new HttpEntity<>(commandDto, gatewayControlHeader(apisixDeploy.getAdminKey(), null));

                // Send PUT request
                ResponseEntity<String> commandResult = sendGatewayHttpCommand(entity, HttpMethod.PUT, url);

                procedureSuccess &= commandResult.getStatusCode().is2xxSuccessful();
            }
        }
        return procedureSuccess;
    }

    // Functions for APISIX Admin
    private HttpHeaders gatewayControlHeader(String adminKey, Map<String, String> optionalHeaders) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", adminKey);
        headers.set("Content-Type", "application/json");

        // Check if optionalHeaders is provided and not empty
        if (MapUtils.isNotEmpty(optionalHeaders)) {
            for (Map.Entry<String, String> header : optionalHeaders.entrySet()) {
                headers.set(header.getKey(), header.getValue());
            }
        }
        return headers;
    }

    @Override
    public boolean deleteGwApikey(String roleId, String apikeyName) throws ApisixProcessorException {
        String gwApikeyName = getGwKeyName(roleId, apikeyName);
        Map<String, ApisixPropertyFormat> siteMap = this.configProperties.getDeployMapBySite();
        if (MapUtils.isNotEmpty(siteMap)) {
            boolean successDelete = false;
            for (ApisixPropertyFormat property : siteMap.values()) {
                String deleteKeyUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, property.getInternalGatewayHost(), property.getInternalGatewayAdminPort(),
                        UriComponentsBuilder.fromPath(MANAGE_APIKEY_URI_WITH_USERNAME).buildAndExpand(gwApikeyName).toString());
                HttpEntity<Object> deleteKeyEntity = new HttpEntity<>(gatewayControlHeader(property.getAdminKey(), null));


                // Send DELETE request
                try {
                    ResponseEntity<String> commandResult = sendGatewayHttpCommand(deleteKeyEntity, HttpMethod.DELETE, deleteKeyUrl);
                    successDelete = commandResult.getStatusCode().is2xxSuccessful() || commandResult.getStatusCode().equals(HttpStatus.NOT_FOUND);
                } catch (HttpClientErrorException e) {
                    if (e.getRawStatusCode() == HttpStatus.NOT_FOUND.value()) {
                        successDelete = true;
                    } else {
                        throw ApisixProcessorException.createExceptionForHttp(HttpStatus.valueOf(e.getRawStatusCode()),
                                ErrorConstantLib.GATEWAY_COMMAND_ERROR.getCompleteMessage());
                    }
                }
                if (!successDelete) {
                    log.info(ErrorConstantLib.GATEWAY_COMMAND_UNABLE_PARSE_JSON.getCompleteMessage());
                    log.info("Unable delete key：" + gwApikeyName);
                    throw ApisixProcessorException.createExceptionForHttp(HttpStatus.INTERNAL_SERVER_ERROR,
                            ErrorConstantLib.GATEWAY_COMMAND_UNABLE_DELETE_KEY.getCompleteMessage());
                }
            }
            return successDelete;
        } else {
            throw ApisixProcessorException.createExceptionForHttp(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorConstantLib.GATEWAY_PROPERTY_PROPERTY_UNLOAD_SITE.getCompleteMessage());
        }
    }

    @Override
    public boolean grantGwApikeyPermission(String targetFabId, String gwRouteId, String roleId, String apikeyName) throws GatewayControllerException {
        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(targetFabId);
        String gwApikeyName = getGwKeyName(roleId, apikeyName);

        //
        List<String> existsWhitelist = getExistsWhiteListOnGateway(targetFabId, gwRouteId);
        existsWhitelist.add(gwApikeyName);

        GwRoutePropertyCommandDto gwRoutePropertyCommandDto =
                GwRoutePropertyCommandDto.builder()
                        .plugins(RoutePluginsDto.builder()
                                .consumerRestriction(apisixRoutePluginDtoFactory.createConsumerRestrictionDto(existsWhitelist))
                                .build())
                        .build();
        String updateInfoUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, connectionInfo.getInternalGatewayHost(), connectionInfo.getInternalGatewayAdminPort(),
                UriComponentsBuilder.fromPath(MANAGE_ROUTE_URI_WITH_ID).buildAndExpand(gwRouteId).toString());
        HttpEntity<Object> updateEntity = new HttpEntity<>(gwRoutePropertyCommandDto, gatewayControlHeader(connectionInfo.getAdminKey(), null));
        // Send retrieve request
        ResponseEntity<String> commandResult = sendGatewayHttpCommand(updateEntity, HttpMethod.PATCH, updateInfoUrl);

        return commandResult.getStatusCode().is2xxSuccessful();
    }

    @Override
    public boolean revokeGwApikeyPermission(String targetFabId, String gwRouteId, String roleId, String apikeyName) throws GatewayControllerException {
        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(targetFabId);
        String gwApikeyName = getGwKeyName(roleId, apikeyName);

        List<String> whitelist = this.getExistsWhiteListOnGateway(targetFabId, gwRouteId);

        if (CollectionUtils.isNotEmpty(whitelist)) {
            // Delete Gw Apikey Permission
            whitelist.remove(gwApikeyName);
            // For Empty List Protection --> Empty List is not allowed on apisix
            if (whitelist.size() < 1) {
                whitelist = ConsumerRestrictionPluginDto.getDefaultWhiteList();
            }
            GwRoutePropertyCommandDto gwRoutePropertyCommandDto =
                    GwRoutePropertyCommandDto.builder()
                            .plugins(
                                    RoutePluginsDto.builder()
                                            .consumerRestriction(
                                                    apisixRoutePluginDtoFactory.createConsumerRestrictionDto(whitelist))
                                            .build())
                            .build();
            String updateInfoUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, connectionInfo.getInternalGatewayHost(), connectionInfo.getInternalGatewayAdminPort(),
                    UriComponentsBuilder.fromPath(MANAGE_ROUTE_URI_WITH_ID).buildAndExpand(gwRouteId).toString());
            HttpEntity<Object> updateEntity = new HttpEntity<>(gwRoutePropertyCommandDto, gatewayControlHeader(connectionInfo.getAdminKey(), null));
            // Send retrieve request
            ResponseEntity<String> commandResult = sendGatewayHttpCommand(updateEntity, HttpMethod.PATCH, updateInfoUrl);

            return commandResult.getStatusCode().is2xxSuccessful();
        }
        return false;
    }

    @Override
    public boolean patchGwRouteDeviceIpListBatch(String targetFabId, List<String> gwRouteIdList, List<String> onGrantDeviceIpList, List<String> onRevokeDeviceIpList) throws GatewayControllerException {
        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(targetFabId);

        boolean isProcedureSuccess = true;
        // Get Data
        for (String gwRouteId : gwRouteIdList.stream().collect(Collectors.toSet())) {
            // 取得當前WhiteList
            List<String> deviceWhitelist = getExistsDeviceWhiteListOnGateway(targetFabId, gwRouteId);
            if (CollectionUtils.isNotEmpty(onGrantDeviceIpList)) {
                deviceWhitelist = (ListUtils.union(deviceWhitelist, onGrantDeviceIpList)).stream().distinct().collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(onRevokeDeviceIpList)) {
                deviceWhitelist = ListUtils.subtract(deviceWhitelist, onRevokeDeviceIpList);
            }

            // Distinct Role Device
            deviceWhitelist = new ArrayList<>(new HashSet<>(deviceWhitelist));

            GwRoutePropertyCommandDto gwRoutePropertyCommandDto =
                    GwRoutePropertyCommandDto.builder()
                            .plugins(
                                    RoutePluginsDto.builder()
                                            .ipRestriction(
                                                    IpRestrictionPluginDto.builder()
                                                            .meta(
                                                                    MetaDto.builder()
                                                                            .disable(!IS_ROLE_DEVICE_MANAGEMENT_ENABLED)
                                                                            .build()
                                                            )
                                                            .whitelist(deviceWhitelist)
                                                            .build())
                                            .build())
                            .build();

            String updateInfoUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, connectionInfo.getInternalGatewayHost(), connectionInfo.getInternalGatewayAdminPort(),
                    UriComponentsBuilder.fromPath(MANAGE_ROUTE_URI_WITH_ID).buildAndExpand(gwRouteId).toString());
            HttpEntity<Object> updateEntity = new HttpEntity<>(gwRoutePropertyCommandDto, gatewayControlHeader(connectionInfo.getAdminKey(), null));
            // Send retrieve request
            ResponseEntity<String> commandResult = sendGatewayHttpCommand(updateEntity, HttpMethod.PATCH, updateInfoUrl);
            isProcedureSuccess &= commandResult.getStatusCode().is2xxSuccessful();
        }
        return isProcedureSuccess;
    }

    private List<String> getExistsDeviceWhiteListOnGateway(String targetFabId, String gwRouteId) throws GatewayControllerException {
        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(targetFabId);

        // Search APISIX Device White List
        String retrieveInfoUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, connectionInfo.getInternalGatewayHost(), connectionInfo.getInternalGatewayAdminPort(),
                UriComponentsBuilder.fromPath(MANAGE_ROUTE_URI_WITH_ID).buildAndExpand(gwRouteId).toString());
        HttpEntity<Object> retrieveEntity = new HttpEntity<>(gatewayControlHeader(connectionInfo.getAdminKey(), null));

        ResponseEntity<String> commandResult = sendGatewayHttpCommand(retrieveEntity, HttpMethod.GET, retrieveInfoUrl);

        GwRouteRetrieveResponseDto gwRouteRetrieveResponseDto;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            gwRouteRetrieveResponseDto = objectMapper.readValue(commandResult.getBody(), GwRouteRetrieveResponseDto.class);
        } catch (IOException e) {
            log.info(ErrorConstantLib.GATEWAY_COMMAND_UNABLE_PARSE_JSON.getCompleteMessage());
            log.info("Unable parse object：" + commandResult.getBody());
            throw ApisixProcessorException.createExceptionForHttp(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorConstantLib.GATEWAY_COMMAND_UNABLE_PARSE_JSON.getCompleteMessage());
        }

        //
        List<String> existsDeviceWhitelist;
        if (gwRouteRetrieveResponseDto != null) {
            Optional<List<String>> gwWhitelist = Optional.ofNullable(gwRouteRetrieveResponseDto)
                    .map(GwRouteRetrieveResponseDto::getValue)
                    .map(GwRouteRetrieveValueTagDto::getPlugins)
                    .map(RoutePluginsDto::getIpRestriction)
                    .map(IpRestrictionPluginDto::getWhitelist);
            if (gwWhitelist.isPresent() && CollectionUtils.isNotEmpty(gwWhitelist.get())) {
                existsDeviceWhitelist = gwWhitelist.get();
            } else {
                existsDeviceWhitelist = new ArrayList<>();
            }
        } else {
            existsDeviceWhitelist = new ArrayList<>();
        }
        return existsDeviceWhitelist;

    }

    private String getCurrentGatewayRouteInfoOnJsonString(String targetFabId, String gwRouteId) throws GatewayControllerException {
        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(targetFabId);

        // Search APISIX Device White List
        String retrieveInfoUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, connectionInfo.getInternalGatewayHost(), connectionInfo.getInternalGatewayAdminPort(),
                UriComponentsBuilder.fromPath(MANAGE_ROUTE_URI_WITH_ID).buildAndExpand(gwRouteId).toString());
        HttpEntity<Object> retrieveEntity = new HttpEntity<>(gatewayControlHeader(connectionInfo.getAdminKey(), null));

        ResponseEntity<String> commandResult = sendGatewayHttpCommand(retrieveEntity, HttpMethod.GET, retrieveInfoUrl);
        String jsonString = commandResult.getBody().toString();
        return StringUtils.defaultString(jsonString, "");
    }

    private List<String> getExistsWhiteListOnGateway(String targetFabId, String gwRouteId) throws GatewayControllerException {
        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(targetFabId);

        // Search APISIX White List
        String retrieveInfoUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, connectionInfo.getInternalGatewayHost(), connectionInfo.getInternalGatewayAdminPort(),
                UriComponentsBuilder.fromPath(MANAGE_ROUTE_URI_WITH_ID).buildAndExpand(gwRouteId).toString());
        HttpEntity<Object> retrieveEntity = new HttpEntity<>(gatewayControlHeader(connectionInfo.getAdminKey(), null));

        ResponseEntity<String> commandResult = sendGatewayHttpCommand(retrieveEntity, HttpMethod.GET, retrieveInfoUrl);

        GwRouteRetrieveResponseDto gwRouteRetrieveResponseDto;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            gwRouteRetrieveResponseDto = objectMapper.readValue(commandResult.getBody(), GwRouteRetrieveResponseDto.class);
        } catch (IOException e) {
            log.info(ErrorConstantLib.GATEWAY_COMMAND_UNABLE_PARSE_JSON.getCompleteMessage());
            log.info("Unable parse object：" + commandResult.getBody());
            throw ApisixProcessorException.createExceptionForHttp(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorConstantLib.GATEWAY_COMMAND_UNABLE_PARSE_JSON.getCompleteMessage());
        }

        //
        List<String> existsWhitelist;
        if (gwRouteRetrieveResponseDto != null) {
            Optional<List<String>> gwWhitelist = Optional.ofNullable(gwRouteRetrieveResponseDto)
                    .map(GwRouteRetrieveResponseDto::getValue)
                    .map(GwRouteRetrieveValueTagDto::getPlugins)
                    .map(RoutePluginsDto::getConsumerRestriction)
                    .map(ConsumerRestrictionPluginDto::getWhitelist);
            if (gwWhitelist.isPresent() && CollectionUtils.isNotEmpty(gwWhitelist.get())) {
                existsWhitelist = gwWhitelist.get();
            } else {
                existsWhitelist = new ArrayList<>();
            }
        } else {
            existsWhitelist = new ArrayList<>();
        }
        return existsWhitelist;
    }

    @Override
    public boolean createOrUpdateGwUpstream(String gwUpstreamId, String systemName, SystemDpyEntity sysDeploy) throws GatewayControllerException {
        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(sysDeploy.getFabId());
        String gwUpstreamName = getGwUpstreamName(sysDeploy.getFabId(), systemName);

        // If ProxyRequired, then we redirect requests to Gateway Proxy Redirector
        List<GwUpstreamSubItemNodeDto> nodeList;
        if (StringUtils.isNotBlank(sysDeploy.getProxyRequired()) &&
                StringUtils.equalsIgnoreCase(sysDeploy.getProxyRequired(), GeneralSetting.GENERAL_POSITIVE_STRING)) {

            // Checking Critical Property First
            if (StringUtils.isBlank(connectionInfo.getGatewayProxyRedirectHost()) ||
                    !validateService.validatePortValid(connectionInfo.getGatewayProxyRedirectPort())) {
                throw new RuntimeException(
                        ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG.getCompleteMessage()
                                + "GatewayProxyRedirectHost/ GatewayProxyRedirectPort"
                );
            }

            // 進行切換，將 Gateway 的路徑切換至 GPR位置(GatewayProxyRedirect)
            // 針對 Gateway Route 的 Rewrite設定，放置於 GatewayRouteControlWatcher
            nodeList = Collections.singletonList(
                    new GwUpstreamSubItemNodeDto(
                            connectionInfo.getGatewayProxyRedirectHost(),
                            connectionInfo.getGatewayProxyRedirectPort(), 1)
            );
        } else {
            nodeList = Collections.singletonList(
                    new GwUpstreamSubItemNodeDto(
                            sysDeploy.getSystemHost(),
                            sysDeploy.getSystemPort(), 1)
            );
        }

        // Build Command
        Integer timeoutLimit = sysDeploy.getTimeoutLimit() != null ? sysDeploy.getTimeoutLimit() : 6;
        GwUpstreamCreateCommandDto gwUpstreamCreateCommandDto = GwUpstreamCreateCommandDto.builder()
                .id(gwUpstreamId)
                .name(gwUpstreamName)
                .nodes(nodeList)
                .timeout(new GwUpstreamSubItemTimeoutDto(timeoutLimit, timeoutLimit, timeoutLimit))
                .scheme(sysDeploy.getScheme()).build();
        if (StringUtils.isNotEmpty(sysDeploy.getHealthCheckPath())) {
            GwUpstreamSubItemChecksDto checks = GwUpstreamSubItemChecksDto.builder()
                    .active(GwUpstreamSubItemActiveCheckDto.builder()
                            .httpPath(sysDeploy.getHealthCheckPath()).build())
                    .passive(GwUpstreamSubItemPassiveCheckDto.builder().build()).build();
            gwUpstreamCreateCommandDto.setChecks(checks);
        }

        String updateInfoUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, connectionInfo.getInternalGatewayHost(), connectionInfo.getInternalGatewayAdminPort(),
                UriComponentsBuilder.fromPath(MANAGE_UPSTREAM_URI_WITH_ID).buildAndExpand(gwUpstreamId).toString());

        log.info("On SentData: {}", JsonStringProcessor.jsonObjectParser(gwUpstreamCreateCommandDto));

        HttpEntity<Object> updateEntity = new HttpEntity<>(gwUpstreamCreateCommandDto, gatewayControlHeader(connectionInfo.getAdminKey(), null));

        // Send retrieve request
        ResponseEntity<String> commandResult = sendGatewayHttpCommand(updateEntity, HttpMethod.PUT, updateInfoUrl);

        return commandResult.getStatusCode().is2xxSuccessful();
    }

    @Override
    public boolean deleteGwUpstream(String undeployFabId, String gwUpstreamId) throws GatewayControllerException {
        if (StringUtils.isEmpty(gwUpstreamId)) {
            throw GatewayControllerException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.GATEWAY_COMMAND_SYSTEM_DELETE_ID_EMPTY.getCompleteMessage());
        }

        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(undeployFabId);

        String deleteInfoUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, connectionInfo.getInternalGatewayHost(), connectionInfo.getInternalGatewayAdminPort(),
                UriComponentsBuilder.fromPath(MANAGE_UPSTREAM_URI_WITH_ID).buildAndExpand(gwUpstreamId).toString());
        HttpEntity<Object> updateEntity = new HttpEntity<>("", gatewayControlHeader(connectionInfo.getAdminKey(), null));

        // Send retrieve request
        ResponseEntity<String> commandResult = sendGatewayHttpCommand(updateEntity, HttpMethod.DELETE, deleteInfoUrl);

        return commandResult.getStatusCode().is2xxSuccessful();
    }

    @Override
    public boolean deleteGwRoute(String undeployFabId, String gwRouteId) throws GatewayControllerException {
        if (StringUtils.isEmpty(gwRouteId)) {
            throw GatewayControllerException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.GATEWAY_COMMAND_ROUTE_DELETE_ID_EMPTY.getCompleteMessage());
        }

        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(undeployFabId);

        String deleteInfoUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, connectionInfo.getInternalGatewayHost(), connectionInfo.getInternalGatewayAdminPort(),
                UriComponentsBuilder.fromPath(MANAGE_ROUTE_URI_WITH_ID).buildAndExpand(gwRouteId).toString());
        HttpEntity<Object> updateEntity = new HttpEntity<>("", gatewayControlHeader(connectionInfo.getAdminKey(), null));

        // Send retrieve request
        ResponseEntity<String> commandResult = sendGatewayHttpCommand(updateEntity, HttpMethod.DELETE, deleteInfoUrl);

        return commandResult.getStatusCode().is2xxSuccessful();
    }

    @Override
    public boolean createGwRoute(String deployFabId, String gwUpstreamId, String gwRouteId, ApiNameDto deployApiName, ApiEndpointEntity endpoint) throws GatewayControllerException {
        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(deployFabId);
        String GwRouteName = getGwRouteName(deployFabId,
                deployApiName.getSystemName(), deployApiName.getApiName(),
                endpoint.getApiItfType());

        // Build Command
        GwRouteCreateInfoDto gwRouteCreateInfoDto = GwRouteCreateInfoDto.builder()
                .uri(getGwSimpleRoutePath(deployFabId, deployApiName.getSystemName(), endpoint.getApiGwUri()))
                .name(GwRouteName)
                .methods(convertHttpMethodStringToList(endpoint.getHttpMethod()))
                .plugins(RoutePluginsDto.builder()
                        .proxyRewritePluginDto(ProxyRewritePluginDto.builder()
                                .uri(endpoint.getApiHostUri())
                                .build())
                        .consumerRestriction(apisixRoutePluginDtoFactory.createConsumerRestrictionDto(
                                ConsumerRestrictionPluginDto.getDefaultWhiteList())
                        )
                        .keyAuth(apisixRoutePluginDtoFactory.createKeyAuthPluginDto()
                        )
                        .ipRestriction(IpRestrictionPluginDto.builder()
                                .meta(
                                        MetaDto.builder()
                                                .disable(!IS_ROLE_DEVICE_MANAGEMENT_ENABLED)
                                                .build()
                                )
                                .whitelist(Arrays.asList("127.0.0.1"))
                                .build()
                        )
                        .build())
                .upstreamId(gwUpstreamId)
                .build();

        String updateInfoUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, connectionInfo.getInternalGatewayHost(), connectionInfo.getInternalGatewayAdminPort(),
                UriComponentsBuilder.fromPath(MANAGE_ROUTE_URI_WITH_ID).buildAndExpand(gwRouteId).toString());
        HttpEntity<Object> updateEntity = new HttpEntity<>(gwRouteCreateInfoDto, gatewayControlHeader(connectionInfo.getAdminKey(), null));

        // Send retrieve request
        ResponseEntity<String> commandResult = sendGatewayHttpCommand(updateEntity, HttpMethod.PUT, updateInfoUrl);

        return commandResult.getStatusCode().is2xxSuccessful();
    }

    @Override
    public boolean updateGwRoute(String deployFabId, String gwRouteId, String systemName, ApiEndpointEntity endpoint) throws GatewayControllerException {
        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(deployFabId);

        // Build Command
        GwRouteCreateInfoDto gwRouteCreateInfoDto = GwRouteCreateInfoDto.builder()
                .uri(getGwSimpleRoutePath(deployFabId, systemName, endpoint.getApiGwUri()))
                .methods(convertHttpMethodStringToList(endpoint.getHttpMethod()))
                .plugins(RoutePluginsDto.builder()
                        .proxyRewritePluginDto(ProxyRewritePluginDto.builder()
                                .uri(endpoint.getApiHostUri())
                                .build())
                        .build())
                .build();
        String updateInfoUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, connectionInfo.getInternalGatewayHost(), connectionInfo.getInternalGatewayAdminPort(),
                UriComponentsBuilder.fromPath(MANAGE_ROUTE_URI_WITH_ID).buildAndExpand(gwRouteId).toString());
        HttpEntity<Object> updateEntity = new HttpEntity<>(gwRouteCreateInfoDto, gatewayControlHeader(connectionInfo.getAdminKey(), null));

        // Send request
        ResponseEntity<String> commandResult = sendGatewayHttpCommand(updateEntity, HttpMethod.PATCH, updateInfoUrl);

        return commandResult.getStatusCode().is2xxSuccessful();

    }

    private String getGwKeyName(@NotNull String roleId, @NotNull String apiKeyName) {
        return GatewayFormatter.formatGwApikeyName(roleId, apiKeyName);
    }

    private String getGwUpstreamName(@NotNull String fabId, @NotNull String systemName) {
        return GatewayFormatter.formatGwUpstreamName(fabId, systemName);
    }

    private String getGwRouteName(@NotNull String fabId, @NotNull String systemName, @NotNull String apiName, @NotNull String postfix) {
        postfix = StringUtils.replaceAll(postfix, "[^a-zA-Z0-9]", "");
        return GatewayFormatter.formatGwRouteName(fabId, systemName, apiName, postfix);
    }

    @Override
    public boolean updateGwRoutePermissionBatch(String fabId, String gwRouteId, List<GwApikeyNameDto> onInitialNameList) throws GatewayControllerException {
        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(fabId);
        List<String> gwApikeyNameList = ConsumerRestrictionPluginDto.getDefaultWhiteList();
        if (CollectionUtils.isNotEmpty(onInitialNameList)) {
            onInitialNameList.stream()
                    .map(gwApikeyNameDto -> getGwKeyName(gwApikeyNameDto.getRoleId(), gwApikeyNameDto.getKeyName()))
                    .forEach(gwApikeyNameList::add);
        }
        GwRoutePropertyCommandDto gwRoutePropertyCommandDto = GwRoutePropertyCommandDto.builder()
                .plugins(RoutePluginsDto.builder()
                        .consumerRestriction(apisixRoutePluginDtoFactory.createConsumerRestrictionDto(gwApikeyNameList))
                        .build())
                .build();
        String updateInfoUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, connectionInfo.getInternalGatewayHost(), connectionInfo.getInternalGatewayAdminPort(),
                UriComponentsBuilder.fromPath(MANAGE_ROUTE_URI_WITH_ID).buildAndExpand(gwRouteId).toString());
        HttpEntity<Object> updateEntity = new HttpEntity<>(gwRoutePropertyCommandDto, gatewayControlHeader(connectionInfo.getAdminKey(), null));

        // Send update request
        ResponseEntity<String> commandResult = sendGatewayHttpCommand(updateEntity, HttpMethod.PATCH, updateInfoUrl);
        return commandResult.getStatusCode().is2xxSuccessful();
    }

    @Override
    public String getGwSimpleRoutePath(String fabId, String systemName, String endpoint) {
        // 檢查並移除參數開頭的斜線
        fabId = this.removeLeadingSlash(fabId);
        systemName = this.removeLeadingSlash(systemName);
        endpoint = this.removeLeadingSlash(endpoint);
        return GatewayFormatter.formatGwSimpleRoutePath(fabId, systemName, endpoint);
    }

    @Override
    public String getExternalGwCompleteRoutePath(String fabId, String systemName, String endpoint) throws GatewayControllerException {
        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(fabId);

        // 檢查並移除參數開頭的斜線
        fabId = this.removeLeadingSlash(fabId);
        systemName = this.removeLeadingSlash(systemName);
        endpoint = this.removeLeadingSlash(endpoint);
        return GatewayFormatter.formatGwCompleteRoutePath(configProperties.getScheme().toLowerCase(),
                connectionInfo.getExternalGatewayHost(),
                connectionInfo.getExternalGatewayServicePort(),
                fabId, systemName, endpoint);
    }

    private String removeLeadingSlash(String value) {
        // 如果字符串以斜線開頭，則移除斜線
        while (value.startsWith("/")) {
            value = value.substring(1);
        }
        return value;
    }

    @Override
    public boolean deployOrUpdateGwPartialPlugin(String fabId, String gwRouteId, GwPluginEntity onMountPlugin, ApiGwPluginDpyEntity pluginDpyData) throws GatewayControllerException {
        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(fabId);

        String commandString = pluginTemplateLoader.getPluginTemplate(onMountPlugin.getGwPluginTemplate()).produceGatewayDeployCommand(pluginDpyData.getGwPluginParameter());
        log.debug("getGwPluginTemplate: {}", pluginTemplateLoader.getPluginTemplate(onMountPlugin.getGwPluginTemplate()).getClass().getSimpleName());
        log.debug("deployOrUpdateGwPartialPlugin commandString: {}", commandString);

        // Build Command Url
        String updateInfoUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, connectionInfo.getInternalGatewayHost(), connectionInfo.getInternalGatewayAdminPort(),
                UriComponentsBuilder.fromPath(MANAGE_ROUTE_URI_WITH_ID).buildAndExpand(gwRouteId).toString());
        HttpEntity<Object> updateEntity = new HttpEntity<>(commandString, gatewayControlHeader(connectionInfo.getAdminKey(), null));


        // Send retrieve request
        ResponseEntity<String> commandResult = sendGatewayHttpCommand(updateEntity, HttpMethod.PATCH, updateInfoUrl);
        log.debug("deployOrUpdateGwPartialPlugin result: {} {}", commandResult.getStatusCode(), commandResult.getBody());
        return commandResult.getStatusCode().is2xxSuccessful();
    }

    @Override
    public boolean undeployGwPlugin(String fabId, String gwRouteId, GwPluginEntity undeployPlugin, ApiGwPluginDpyEntity pluginDpyData) throws GatewayControllerException {
        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(fabId);

        // Build Command
        String gwRouteData = getCurrentGatewayRouteInfoOnJsonString(fabId, gwRouteId);
        if (StringUtils.isNotBlank(gwRouteData)) {
            String commandString = pluginTemplateLoader.getPluginTemplate(undeployPlugin.getGwPluginTemplate()).produceGatewayUndeployCommand(gwRouteData, pluginDpyData.getGwPluginParameter());

            // Build Command Url
            String updateInfoUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, connectionInfo.getInternalGatewayHost(), connectionInfo.getInternalGatewayAdminPort(),
                    UriComponentsBuilder.fromPath(MANAGE_ROUTE_URI_WITH_ID).buildAndExpand(gwRouteId).toString());
            HttpEntity<Object> updateEntity = new HttpEntity<>(commandString, gatewayControlHeader(connectionInfo.getAdminKey(), null));

            // Send retrieve request
            ResponseEntity<String> commandResult = sendGatewayHttpCommand(updateEntity, HttpMethod.PATCH, updateInfoUrl);

            return commandResult.getStatusCode().is2xxSuccessful();
        }
        return false;
    }

    private ResponseEntity<String> sendGatewayHttpCommand(HttpEntity<Object> httpEntity, HttpMethod httpMethod, String url) throws ApisixProcessorException {
        try {
            return this.sendHttpCommand(httpEntity, httpMethod, url, String.class);
        } catch (HttpClientErrorException e) {
            log.error("Error Response Status Code: {}", e.getStatusCode());
            log.error("Error Response Body: {}", "APISIX：" + e.getResponseBodyAsString());
            throw ApisixProcessorException.createExceptionForHttp(e.getStatusCode(), e.getResponseBodyAsString()); // Re-throw the exception to let the caller handle it, or you can handle it based on your application's requirements
        } catch (Exception e) {
            log.error("An unexpected error occurred during {} processing, for sending the HTTP request to URL: {}", Thread.currentThread().getStackTrace()[2].getMethodName(), url, e);
            throw e; // Generic catch to handle other types of exceptions
        }
    }

    @Override
    public ExternalGatewayInfoListDto provideExternalGatewayInfoList() {
        List<ExternalGatewayInfoDto> gatewayInfoList = new ArrayList<>();
        Map<String, ApisixPropertyFormat> apisixBySiteMap = configProperties.getDeployMapBySite();
        apisixBySiteMap.values().forEach(
                apisixPropertyFormat -> {
                    gatewayInfoList.add(
                            ExternalGatewayInfoDto.builder()
                                    .enableHttps(IS_USE_HTTPS)
                                    .extGatewayHost(apisixPropertyFormat.getExternalGatewayHost())
                                    .extGatewayPort(apisixPropertyFormat.getExternalGatewayServicePort())
                                    .extGrafanaHost(apisixPropertyFormat.getExternalGrafanaHost())
                                    .extGrafanaPort(apisixPropertyFormat.getExternalGrafanaPort())
                                    .fabList(apisixPropertyFormat.getFab())
                                    .build()
                    );
                }
        );

        return ExternalGatewayInfoListDto.builder()
                .externalGatewayInfoDtoList(gatewayInfoList)
                .build();
    }
}
