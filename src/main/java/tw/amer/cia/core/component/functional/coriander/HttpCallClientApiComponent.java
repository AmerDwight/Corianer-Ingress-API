package tw.amer.cia.core.component.functional.coriander;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import tw.amer.cia.core.component.structural.annotation.HostComponent;
import tw.amer.cia.core.component.structural.httpClient.HttpRequestSender;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.model.database.*;
import tw.amer.cia.core.model.database.SystemEntity;
import tw.amer.cia.core.model.database.compositeId.RoleAuthoroityEntityId;
import tw.amer.cia.core.model.pojo.component.property.ClientDeployProperty;
import tw.amer.cia.core.model.pojo.component.property.ClientDeployPropertyFormat;
import tw.amer.cia.core.model.pojo.component.property.NodeStatusDTO;
import tw.amer.cia.core.model.pojo.component.property.VersionSignature;
import tw.amer.cia.core.model.pojo.service.common.api.CreateOrUpdateApiEndpointDto;
import tw.amer.cia.core.model.pojo.service.common.gateway.ExternalGatewayInfoDto;
import tw.amer.cia.core.model.pojo.service.common.gateway.ExternalGatewayInfoListDto;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;


@Data
@ConfigurationProperties(prefix = "deployment")
@HostComponent
@Slf4j
public class HttpCallClientApiComponent extends HttpRequestSender implements CallClientApiComponent {
    // Client Control Type
    private final String CLIENT_ADMIN_RESTFUL_SYSTEM_URI = "/restful/admin/system";
    private final String CLIENT_ADMIN_RESTFUL_SYSTEM_DEPLOYMENT_URI = "/restful/admin/system/deployment";
    private final String CLIENT_ADMIN_RESTFUL_API_URI = "/restful/admin/api";
    private final String CLIENT_ADMIN_RESTFUL_BROADCAST_API_URI = "/restful/admin/broadcast/api";
    private final String CLIENT_ADMIN_RESTFUL_BROADCAST_API_PLUGIN_URI = "/restful/admin/broadcast/api/plugin";
    private final String CLIENT_ADMIN_RESTFUL_API_ENDPOINT_URI = "/restful/admin/api/endpoint";
    private final String CLIENT_ADMIN_RESTFUL_API_DEPLOYMENT_URI = "/restful/admin/api/deployment";
    private final String CLIENT_ADMIN_RESTFUL_APIKEY_URI = "/restful/admin/apikey";
    private final String CLIENT_ADMIN_RESTFUL_APIKEY_PERMISSION_URI = "/restful/admin/apikey/permission";
    private final String CLIENT_ADMIN_RESTFUL_BROADCAST_APIKEY_STATUS_CHANGE_URI = "/restful/admin/broadcast/apikey/active/change";
    private final String CLIENT_ADMIN_RESTFUL_ROLE_URI = "/restful/admin/role";
    private final String CLIENT_ADMIN_RESTFUL_ROLE_AUTHORITY_URI = "/restful/admin/role/authority";
    private final String CLIENT_ADMIN_RESTFUL_ROLE_DEVICE_URI = "/restful/admin/role/device";
    private final String CLIENT_ADMIN_RESTFUL_GW_PLUGIN_URI = "/restful/admin/api/plugin";

    // Host Ask Client Information
    private final String SYNCHRONIZE_INFORMATION_GATEWAY = "/synchronize/data/gateway";

    // Common Restful Db coriander
    private final String RESTFUL_DB_URI_SYSTEM = "/database/sync/SystemEntity";
    private final String RESTFUL_DB_URI_API = "/database/sync/ApiEntity";
    private final String RESTFUL_DB_URI_API_ENDPOINT = "/database/sync/ApiEndpointEntity";
    private final String RESTFUL_DB_URI_ROLE_AUTHORITY = "/database/sync/RoleAuthorityEntity";
    private final String RESTFUL_DB_URI_GW_PLUGIN = "/database/sync/GwPluginEntity";
    private final String RESTFUL_DB_URI_EXTERNAL_SYSTEM_CONFIG = "/database/sync/ExternalSystemConfigEntity";

    // Setup
    private String deployType;
    private boolean IS_USE_HTTPS;

    public HttpCallClientApiComponent() {
        super(true);
    }

    @PostConstruct
    public void initHttpCallClientApiComponent() {
        this.deployType = coreProperties.getSetting().getDeployType();
        this.IS_USE_HTTPS = StringUtils.equalsIgnoreCase("HTTPS", coreProperties.getSetting().getScheme());
    }

    @Override
    public void createOrUpdateSystem(String fabId, SystemEntity dtoToClient) throws CiaProcessorException {
        // Prepare Data
        ClientDeployPropertyFormat clientDeployPropertyFormat = coreProperties.getClientPropertiesByFab(fabId);

        for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
            String url = buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                    clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_SYSTEM_URI);
            HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

            // Send PATCH request
            ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.POST, url);
        }
    }

    @Override
    public void createOrUpdateSystemDeployment(String fabId, SystemDpyEntity dtoToClient) throws CiaProcessorException {
        // Prepare Data
        ClientDeployPropertyFormat clientDeployPropertyFormat = coreProperties.getClientPropertiesByFab(fabId);

        for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
            String url = buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                    clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_SYSTEM_DEPLOYMENT_URI);
            HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

            // Send PATCH request
            ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.POST, url);
        }
    }

    @Override
    public void deleteSystemDeployment(String fabId, SystemDpyEntity dtoToClient) throws CiaProcessorException {
        // Prepare Data
        ClientDeployPropertyFormat clientDeployPropertyFormat = coreProperties.getClientPropertiesByFab(fabId);

        for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
            String url = buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                    clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_SYSTEM_DEPLOYMENT_URI);
            HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

            // Send DELETE request
            ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.DELETE, url);
        }
    }

    @Override
    public void updateSystemToDeployedClients(List<String> fabList, SystemEntity dtoToClient) throws CiaProcessorException {
        Set<ClientDeployPropertyFormat> onUpdateClients = this.findClientsByFabList(fabList);

        if (CollectionUtils.isNotEmpty(onUpdateClients)) {
            for (ClientDeployPropertyFormat clientDeployPropertyFormat : onUpdateClients) {

                for (ClientDeployProperty client : clientDeployPropertyFormat.getDeployList()) {
                    // Prepare Data
                    String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, client.getClientDns(),
                            client.getClientPort(), RESTFUL_DB_URI_SYSTEM +
                                    "/" + dtoToClient.getSystemId());
                    HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(client.getAdminKey().get(0), null));

                    // Send PUT request
                    ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.PUT, url);
                }
            }
        }
    }

    @Override
    public void createOrUpdateApi(String fabId, ApiEntity dtoToClient) throws CiaProcessorException {
        // Prepare Data
        ClientDeployPropertyFormat clientDeployPropertyFormat = coreProperties.getClientPropertiesByFab(fabId);

        for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
            String url = buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                    clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_API_URI);
            HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

            // Send PATCH request
            ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.POST, url);
        }
    }

    @Override
    public void createOrUpdateApiEndpoint(String fabId, CreateOrUpdateApiEndpointDto dtoToClient) throws CiaProcessorException {
        // Prepare Data
        ClientDeployPropertyFormat clientDeployPropertyFormat = coreProperties.getClientPropertiesByFab(fabId);

        for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
            String url = buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                    clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_API_ENDPOINT_URI);
            HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

            // Send PATCH request
            ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.POST, url);
        }
    }

    @Override
    public void createOrUpdateApiDeployment(String fabId, ApiDpyEntity dtoToClient) throws CiaProcessorException {
        // Prepare Data
        ClientDeployPropertyFormat clientDeployPropertyFormat = coreProperties.getClientPropertiesByFab(fabId);

        for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
            String url = buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                    clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_API_DEPLOYMENT_URI);
            HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

            // Send PATCH request
            ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.POST, url);

        }
    }

    @Override
    public void deleteApiDeployment(String fabId, ApiDpyEntity dtoToClient) throws CiaProcessorException {
        // Prepare Data
        ClientDeployPropertyFormat clientDeployPropertyFormat = coreProperties.getClientPropertiesByFab(fabId);

        for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
            String url = buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                    clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_API_DEPLOYMENT_URI);
            HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

            // Send DELETE request
            ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.DELETE, url);

        }
    }

    @Override
    public void updateApiToDeployedClients(List<String> fabList, ApiEntity dtoToClient) throws CiaProcessorException {
        Set<ClientDeployPropertyFormat> onUpdateClients = this.findClientsByFabList(fabList);

        if (CollectionUtils.isNotEmpty(onUpdateClients)) {
            for (ClientDeployPropertyFormat clientDeployPropertyFormat : onUpdateClients) {
                for (ClientDeployProperty client : clientDeployPropertyFormat.getDeployList()) {
                    // Prepare Data
                    String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, client.getClientDns(),
                            client.getClientPort(), RESTFUL_DB_URI_API +
                                    "/" + dtoToClient.getApiId());
                    HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(client.getAdminKey().get(0), null));

                    // Send PUT request
                    ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.PUT, url);
                }
            }
        }
    }

    @Override
    public void updateApiEndpointToDeployedClients(List<String> fabList, CreateOrUpdateApiEndpointDto dtoToClient) throws CiaProcessorException {
        Set<ClientDeployPropertyFormat> onUpdateClients = this.findClientsByFabList(fabList);

        if (CollectionUtils.isNotEmpty(onUpdateClients)) {
            for (ClientDeployPropertyFormat clientDeployPropertyFormat : onUpdateClients) {
                for (ClientDeployProperty client : clientDeployPropertyFormat.getDeployList()) {
                    String url = buildUrl(IS_USE_HTTPS, client.getClientDns(),
                            client.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_API_ENDPOINT_URI);
                    HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(client.getAdminKey().get(0), null));

                    // Send PATCH request
                    ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.POST, url);
                }
            }
        }
    }

    private Set<ClientDeployPropertyFormat> findClientsByFabList(List<String> fabList) throws CiaProcessorException {
        Set<ClientDeployPropertyFormat> clientsList = new HashSet<>();
        for (String fabId : fabList) {
            ClientDeployPropertyFormat client = this.coreProperties.getClientPropertiesByFab(fabId);
            clientsList.add(client);
        }
        return clientsList;
    }


    // Apikey Management
    @Override
    public void createOrUpdateApikeyFromHost(String fabId, GwApikeyEntity dtoToClient) throws CiaProcessorException {
        // Prepare Data
        ClientDeployPropertyFormat clientDeployPropertyFormat = coreProperties.getClientPropertiesByFab(fabId);

        for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
            String url = buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                    clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_APIKEY_URI);
            HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

            // Send PATCH request
            ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.POST, url);
        }
    }

    @Override
    public void createOrUpdateApikeyFromHostExclude(String fabId, String excludeIdentifier, GwApikeyEntity dtoToClient) throws CiaProcessorException {
        // Prepare Data
        ClientDeployPropertyFormat clientDeployPropertyFormat = coreProperties.getClientPropertiesByFab(fabId);
        for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
            boolean isNotExcluded = !StringUtils.equalsIgnoreCase(clientDeployProperty.getIdentify(), excludeIdentifier);
            log.debug("Checking client {} and exclude {} ", clientDeployProperty.getIdentify(), excludeIdentifier);
            if (isNotExcluded) {
                log.info("Independently Calling Command to {}", clientDeployProperty.getIdentify());
                String url = buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                        clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_APIKEY_URI);
                HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

                // Send PATCH request
                ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.POST, url);
            } else {
                log.info("Excluded Command to {}", clientDeployProperty.getIdentify());
            }
        }
    }

    @Override
    public void manageApikeyPermissionFromHostBatchThroughFabId(String fabId, List<GwApikeyPermissionEntity> revokePermissionList, List<GwApikeyPermissionEntity> grantPermissionList) throws CiaProcessorException {
        // Prepare Data
        ClientDeployPropertyFormat clientDeployPropertyFormat = coreProperties.getClientPropertiesByFab(fabId);
        this.manageApikeyPermissionFromHostBatch(clientDeployPropertyFormat, revokePermissionList, grantPermissionList);
    }

    @Override
    public void manageApikeyPermissionFromHostBatchThroughFabListExclude(String fabId, String excludeIdentifier, List<GwApikeyPermissionEntity> revokePermissionList, List<GwApikeyPermissionEntity> grantPermissionList) throws CiaProcessorException {
        // Prepare Data
        ClientDeployPropertyFormat clientDeployPropertyFormat = coreProperties.getClientPropertiesByFab(fabId);
        this.manageApikeyPermissionFromHostBatchExclude(clientDeployPropertyFormat, excludeIdentifier, revokePermissionList, grantPermissionList);
    }

    public void manageApikeyPermissionFromHostBatch(ClientDeployPropertyFormat clientDeployPropertyFormat, List<GwApikeyPermissionEntity> revokePermissionList, List<GwApikeyPermissionEntity> grantPermissionList) throws CiaProcessorException {
        for (ClientDeployProperty client : clientDeployPropertyFormat.getDeployList()) {
            // Prepare Data
            String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, client.getClientDns(),
                    client.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_APIKEY_PERMISSION_URI);
            HttpEntity<Object> entity;
            ResponseEntity<String> commandResult;

            if (CollectionUtils.isNotEmpty(grantPermissionList)) {
                // Prepare Grant
                entity = new HttpEntity<>(grantPermissionList, apiControlHeader(client.getAdminKey().get(0), null));
                // Send POST request
                commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.POST, url);
            }

            if (CollectionUtils.isNotEmpty(revokePermissionList)) {
                // Prepare Revoke
                entity = new HttpEntity<>(revokePermissionList, apiControlHeader(client.getAdminKey().get(0), null));
                // Send DELETE request
                commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.DELETE, url);
            }
        }
    }

    public void manageApikeyPermissionFromHostBatchExclude(ClientDeployPropertyFormat clientDeployPropertyFormat, String excludeIdentifier,
                                                           List<GwApikeyPermissionEntity> revokePermissionList, List<GwApikeyPermissionEntity> grantPermissionList) throws CiaProcessorException {
        for (ClientDeployProperty client : clientDeployPropertyFormat.getDeployList()) {
            boolean isNotExclude = !StringUtils.equalsIgnoreCase(client.getIdentify(), excludeIdentifier);
            log.debug("Checking client {} and exclude {} ", client.getIdentify(), excludeIdentifier);
            if (isNotExclude) {
                log.info("Independently Calling Command to {}", client.getIdentify());
                // Prepare Data
                String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, client.getClientDns(),
                        client.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_APIKEY_PERMISSION_URI);
                HttpEntity<Object> entity;
                ResponseEntity<String> commandResult;

                if (CollectionUtils.isNotEmpty(grantPermissionList)) {
                    // Prepare Grant
                    entity = new HttpEntity<>(grantPermissionList, apiControlHeader(client.getAdminKey().get(0), null));
                    // Send POST request
                    commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.POST, url);
                }

                if (CollectionUtils.isNotEmpty(revokePermissionList)) {
                    // Prepare Revoke
                    entity = new HttpEntity<>(revokePermissionList, apiControlHeader(client.getAdminKey().get(0), null));
                    // Send DELETE request
                    commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.DELETE, url);
                }
            } else {
                log.info("Excluded Command to {}", client.getIdentify());
            }
        }
    }

    // Role Management
    @Override
    public void createOrUpdateRole(String fabId, RoleEntity dtoToClient) throws CiaProcessorException {
        // Prepare Data
        ClientDeployPropertyFormat clientDeployPropertyFormat = coreProperties.getClientPropertiesByFab(fabId);

        for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
            String url = buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                    clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_ROLE_URI);
            HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

            // Send PATCH request
            ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.POST, url);
        }
    }

    @Override
    public void createOrUpdateRoleAuthority(String fabId, List<RoleAuthorityEntity> dtoToClient) throws CiaProcessorException {
        // Prepare Data
        ClientDeployPropertyFormat clientDeployPropertyFormat = coreProperties.getClientPropertiesByFab(fabId);

        for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
            String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                    clientDeployProperty.getClientPort(), RESTFUL_DB_URI_ROLE_AUTHORITY + "/batch");
            HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

            // Send POST request
            ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.POST, url);
        }
    }

    @Override
    public void updateRoleToDeployedClients(List<String> fabList, RoleEntity dtoToClient) throws CiaProcessorException {
        Set<ClientDeployPropertyFormat> onUpdateClients = this.findClientsByFabList(fabList);

        if (CollectionUtils.isNotEmpty(onUpdateClients)) {
            for (ClientDeployPropertyFormat clientDeployPropertyFormat : onUpdateClients) {
                for (ClientDeployProperty client : clientDeployPropertyFormat.getDeployList()) {
                    String url = buildUrl(IS_USE_HTTPS, client.getClientDns(),
                            client.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_ROLE_URI);
                    HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(client.getAdminKey().get(0), null));

                    // Send PATCH request
                    ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.POST, url);
                }
            }
        }
    }

    @Override
    public void deleteRoleAuthorityToDeployedClients(List<String> fabList, Map<String, List<RoleAuthoroityEntityId>> dtoToClientHm) throws CiaProcessorException {
        Set<ClientDeployPropertyFormat> onUpdateClients = this.findClientsByFabList(fabList);

        if (CollectionUtils.isNotEmpty(onUpdateClients)) {
            for (ClientDeployPropertyFormat clientDeployPropertyFormat : onUpdateClients) {
                Set<String> scopedFabSet = new HashSet<>(clientDeployPropertyFormat.getFab());

                List<RoleAuthoroityEntityId> dataListToClient = new ArrayList<>();
                for (String fabId : scopedFabSet) {
                    List<RoleAuthoroityEntityId> dataByFab = dtoToClientHm.get(fabId);
                    if (CollectionUtils.isNotEmpty(dataByFab)) {
                        dataListToClient.addAll(dataByFab);
                    }
                }
                if (CollectionUtils.isNotEmpty(dataListToClient)) {
                    for (ClientDeployProperty client : clientDeployPropertyFormat.getDeployList()) {
                        String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, client.getClientDns(),
                                client.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_ROLE_AUTHORITY_URI);
                        HttpEntity<Object> entity = new HttpEntity<>(dataListToClient, apiControlHeader(client.getAdminKey().get(0), null));

                        // Send DELETE request
                        ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.DELETE, url);
                    }
                }
            }
        }
    }

    @Override
    public void deleteRoleAuthority(String fabId, List<RoleAuthorityEntity> dtoToClient) throws CiaProcessorException {
        // Prepare Data
        ClientDeployPropertyFormat clientDeployPropertyFormat = coreProperties.getClientPropertiesByFab(fabId);

        List<RoleAuthoroityEntityId> sentInIdList =
                dtoToClient.stream()
                        .map(obj -> new RoleAuthoroityEntityId(obj.getRoleId(), obj.getApiId(), obj.getFabId()))
                        .collect(Collectors.toList());
        for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
            String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                    clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_ROLE_AUTHORITY_URI);
            HttpEntity<Object> entity = new HttpEntity<>(sentInIdList, apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

            // Send DELETE request
            ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.DELETE, url);
        }
    }

    @Override
    public void createOrUpdateRoleDeviceFromHost(String fabId, RoleDeviceEntity dtoToClient) throws CiaProcessorException {
        // Prepare Data
        ClientDeployPropertyFormat clientDeployPropertyFormat = coreProperties.getClientPropertiesByFab(fabId);

        for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
            String url = buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                    clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_ROLE_DEVICE_URI);
            HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

            // Send PATCH request
            ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.POST, url);
        }
    }

    @Override
    public void deleteRoleDevice(String fabId, String deviceId) throws CiaProcessorException {
        // Prepare Data
        ClientDeployPropertyFormat clientDeployPropertyFormat = coreProperties.getClientPropertiesByFab(fabId);

        for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
            String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                    clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() +
                            CLIENT_ADMIN_RESTFUL_ROLE_DEVICE_URI + "/" + deviceId);
            HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

            // Send DELETE request
            ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.DELETE, url);
        }
    }

    @Override
    @Async
    public Map<String, Boolean> tryDeleteApiBroadcast(String apiId) throws CiaProcessorException {
        Map<String, ClientDeployPropertyFormat> clientDeployPropertiesMap = coreProperties.getClientDeployMapBySite();
        Map<String, HttpEntity<Object>> urlAndHttpEntityPairMap = new HashMap<>();
        Map<String, Boolean> resultMap = new HashMap<>();

        for (String siteName : clientDeployPropertiesMap.keySet()) {
            ClientDeployPropertyFormat clientDeployPropertyFormat = clientDeployPropertiesMap.get(siteName);
            for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
                String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                        clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_BROADCAST_API_URI + "/" + apiId);
                HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

                urlAndHttpEntityPairMap.put(url, entity);
            }
        }
        if (MapUtils.isNotEmpty(urlAndHttpEntityPairMap)) {
            resultMap = this.broadcastSender(urlAndHttpEntityPairMap, HttpMethod.DELETE);
        }

        return resultMap;
    }

    @Override
    @Async
    public Map<String, List<NodeStatusDTO>> tryCheckAllClientAliveBroadcast() throws CiaProcessorException {
        log.info("Starting to check all client alive status");
        Map<String, ClientDeployPropertyFormat> clientDeployPropertiesMap = coreProperties.getClientDeployMapBySite();
        Map<String, HttpEntity<Object>> httpEntityByUrlMap = new HashMap<>();
        Map<String, List<ClientNodeInfo>> nodeInfoBySiteMap = new HashMap<>();
        Map<String, List<NodeStatusDTO>> resultMap = new HashMap<>();
        // 收集所有節點信息
        for (String siteName : clientDeployPropertiesMap.keySet()) {
            ClientDeployPropertyFormat clientDeployPropertyFormat = clientDeployPropertiesMap.get(siteName);
            for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
                String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                        clientDeployProperty.getClientPort(), "/version");
                HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));
                httpEntityByUrlMap.put(url, entity);

                // 保存節點信息，包括URL、DNS和端口
                nodeInfoBySiteMap.computeIfAbsent(siteName, k -> new ArrayList<>())
                        .add(new ClientNodeInfo(url, clientDeployProperty.getClientDns(), String.valueOf(clientDeployProperty.getClientPort())));

                log.info("URL built and entity created for site: {}, URL: {}", siteName, url);
            }
        }

        if (MapUtils.isNotEmpty(httpEntityByUrlMap)) {
            log.info("Broadcasting to all clients to check availability");
            Map<String, VersionSignature> broadcastMap = this.broadcastSender(httpEntityByUrlMap, HttpMethod.GET, VersionSignature.class);

            // 處理每個站點的節點狀態
            for (String siteName : nodeInfoBySiteMap.keySet()) {
                List<NodeStatusDTO> siteNodeStatuses = new ArrayList<>();

                for (ClientNodeInfo nodeInfo : nodeInfoBySiteMap.get(siteName)) {
                    String url = nodeInfo.getUrl();
                    boolean isAlive = broadcastMap.containsKey(url) && broadcastMap.get(url) != null;

                    NodeStatusDTO nodeStatus = NodeStatusDTO.builder()
                            .clientDns(nodeInfo.getClientDns())
                            .clientPort(nodeInfo.getClientPort())
                            .isAlive(isAlive)
                            .versionInfo(isAlive ? broadcastMap.get(url) : null)
                            .build();

                    siteNodeStatuses.add(nodeStatus);

                    log.info("Node at {} is {}", url, isAlive ? "alive" : "down");
                }

                resultMap.put(siteName, siteNodeStatuses);
            }
        } else {
            log.warn("No URLs generated to check client status");
        }

        log.info("Completed checking all client alive status");
        return resultMap;
    }
    /**
     * @return Map<String, List < ExternalGatewayInfoDto>> key = site
     * @throws CiaProcessorException
     */
    @Override
    @Async
    public Map<String, List<ExternalGatewayInfoDto>> tryLoadAllClientGatewayInfoBroadcast() throws CiaProcessorException {
        log.info("Starting to load all client bundled gateway service.");
        Map<String, ClientDeployPropertyFormat> clientDeployPropertieBySiteMap = coreProperties.getClientDeployMapBySite();
        Map<String, HttpEntity<Object>> httpEntityByUrlMap = new HashMap<>();
        Map<String, String> urlBySiteMap = new HashMap<>();
        Map<String, List<ExternalGatewayInfoDto>> resultMap = new HashMap<>();

        for (String siteName : clientDeployPropertieBySiteMap.keySet()) {
            ClientDeployPropertyFormat clientDeployPropertyFormat = clientDeployPropertieBySiteMap.get(siteName);
            String url = null;
            for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
                url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                        clientDeployProperty.getClientPort(), SYNCHRONIZE_INFORMATION_GATEWAY);
                HttpEntity<Object> entity = new HttpEntity<>(apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

                httpEntityByUrlMap.put(url, entity);
                log.info("URL built and entity created for site: {}, URL: {}", siteName, url);
            }
            urlBySiteMap.put(siteName, url);
        }
        if (MapUtils.isNotEmpty(httpEntityByUrlMap)) {
            log.info("Broadcasting to all clients to load gateway information");
            Map<String, ExternalGatewayInfoListDto> broadcastMap = this.broadcastSender(httpEntityByUrlMap, HttpMethod.GET, ExternalGatewayInfoListDto.class);
            if (MapUtils.isNotEmpty(broadcastMap)) {
                for (String siteName : urlBySiteMap.keySet()) {
                    if (broadcastMap.containsKey(urlBySiteMap.get(siteName)) && (broadcastMap.get(urlBySiteMap.get(siteName)) != null)) {
                        resultMap.put(siteName, broadcastMap.get(urlBySiteMap.get(siteName)).getExternalGatewayInfoDtoList());
                        log.info("Site {} get gateway info = {} ", siteName, resultMap.get(siteName));
                    } else {
                        log.warn("No response for site: {}", siteName);
                    }
                }
            }
        } else {
            log.warn("No URLs generated to load client data");
        }
        log.info("Completed loading client data.");
        return resultMap;
    }

    @Override
    public Map<String, Boolean> createOrUpdateGwPluginBroadcast(GwPluginEntity dtoToClient) {
        Map<String, ClientDeployPropertyFormat> clientDeployPropertiesMap = coreProperties.getClientDeployMapBySite();
        Map<String, HttpEntity<Object>> urlAndHttpEntityPairMap = new HashMap<>();
        Map<String, Boolean> resultMap = new HashMap<>();

        for (String siteName : clientDeployPropertiesMap.keySet()) {
            ClientDeployPropertyFormat clientDeployPropertyFormat = clientDeployPropertiesMap.get(siteName);
            for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
                String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                        clientDeployProperty.getClientPort(), RESTFUL_DB_URI_GW_PLUGIN);
                HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

                urlAndHttpEntityPairMap.put(url, entity);
            }
        }
        if (MapUtils.isNotEmpty(urlAndHttpEntityPairMap)) {
            resultMap = this.broadcastSender(urlAndHttpEntityPairMap, HttpMethod.POST);
        }

        return resultMap;
    }

    @Override
    public Map<String, Boolean> createOrUpdateApiPluginBroadcast(ApiGwPluginDpyEntity dtoToClient) throws CiaProcessorException {
        Map<String, ClientDeployPropertyFormat> clientDeployPropertiesMap = coreProperties.getClientDeployMapBySite();
        Map<String, HttpEntity<Object>> urlAndHttpEntityPairMap = new HashMap<>();
        Map<String, Boolean> resultMap = new HashMap<>();

        for (String siteName : clientDeployPropertiesMap.keySet()) {
            ClientDeployPropertyFormat clientDeployPropertyFormat = clientDeployPropertiesMap.get(siteName);
            for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
                String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                        clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() + CLIENT_ADMIN_RESTFUL_BROADCAST_API_PLUGIN_URI);
                HttpEntity<Object> entity = new HttpEntity<>(dtoToClient, apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

                urlAndHttpEntityPairMap.put(url, entity);
            }
        }
        if (MapUtils.isNotEmpty(urlAndHttpEntityPairMap)) {
            resultMap = this.broadcastSender(urlAndHttpEntityPairMap, HttpMethod.POST);
        }

        return resultMap;
    }

    @Override
    public Map<String, Boolean> undeployGwPluginBroadcast(String gwPluginId) throws CiaProcessorException {
        Map<String, ClientDeployPropertyFormat> clientDeployPropertiesMap = coreProperties.getClientDeployMapBySite();
        Map<String, HttpEntity<Object>> urlAndHttpEntityPairMap = new HashMap<>();
        Map<String, Boolean> resultMap = new HashMap<>();

        for (String siteName : clientDeployPropertiesMap.keySet()) {
            ClientDeployPropertyFormat clientDeployPropertyFormat = clientDeployPropertiesMap.get(siteName);
            for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
                String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                        clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() +
                                RESTFUL_DB_URI_GW_PLUGIN + "/" + gwPluginId);
                HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

                urlAndHttpEntityPairMap.put(url, entity);
            }
        }
        if (MapUtils.isNotEmpty(urlAndHttpEntityPairMap)) {
            resultMap = this.broadcastSender(urlAndHttpEntityPairMap, HttpMethod.DELETE);
        }
        return resultMap;
    }

    @Override
    @Async
    public void tryUpdateApikeyActiveStatusBroadcastNoReply(String apikeyId) throws CiaProcessorException {
        Map<String, ClientDeployPropertyFormat> clientDeployPropertiesMap = coreProperties.getClientDeployMapBySite();
        Map<String, HttpEntity<Object>> urlAndHttpEntityPairMap = new HashMap<>();

        for (String siteName : clientDeployPropertiesMap.keySet()) {
            ClientDeployPropertyFormat clientDeployPropertyFormat = clientDeployPropertiesMap.get(siteName);
            for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
                String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                        clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() +
                                CLIENT_ADMIN_RESTFUL_BROADCAST_APIKEY_STATUS_CHANGE_URI + "/" + apikeyId);
                HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

                urlAndHttpEntityPairMap.put(url, entity);
            }
        }
        if (MapUtils.isNotEmpty(urlAndHttpEntityPairMap)) {
            this.broadcastSender(urlAndHttpEntityPairMap, HttpMethod.PUT);
        }
    }

    @Override
    @Async
    public void tryUpdateExtEntityBroadcastNoReply(ExternalSystemConfigEntity onUpdateObj) {
        Map<String, ClientDeployPropertyFormat> clientDeployPropertiesMap = coreProperties.getClientDeployMapBySite();
        Map<String, HttpEntity<Object>> urlAndHttpEntityPairMap = new HashMap<>();

        for (String siteName : clientDeployPropertiesMap.keySet()) {
            ClientDeployPropertyFormat clientDeployPropertyFormat = clientDeployPropertiesMap.get(siteName);
            for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
                String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                        clientDeployProperty.getClientPort(), RESTFUL_DB_URI_EXTERNAL_SYSTEM_CONFIG);
                HttpEntity<Object> entity = new HttpEntity<>(onUpdateObj, apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

                urlAndHttpEntityPairMap.put(url, entity);
            }
        }
        if (MapUtils.isNotEmpty(urlAndHttpEntityPairMap)) {
            this.broadcastSender(urlAndHttpEntityPairMap, HttpMethod.PUT);
        }
    }

    @Override
    @Async
    public void tryDeleteExtEntityBroadcastNoReply(String extEntityId) {
        Map<String, ClientDeployPropertyFormat> clientDeployPropertiesMap = coreProperties.getClientDeployMapBySite();
        Map<String, HttpEntity<Object>> urlAndHttpEntityPairMap = new HashMap<>();

        for (String siteName : clientDeployPropertiesMap.keySet()) {
            ClientDeployPropertyFormat clientDeployPropertyFormat = clientDeployPropertiesMap.get(siteName);
            for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
                String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                        clientDeployProperty.getClientPort(), RESTFUL_DB_URI_EXTERNAL_SYSTEM_CONFIG + "/" + extEntityId);
                HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

                urlAndHttpEntityPairMap.put(url, entity);
            }
        }
        if (MapUtils.isNotEmpty(urlAndHttpEntityPairMap)) {
            this.broadcastSender(urlAndHttpEntityPairMap, HttpMethod.DELETE);
        }
    }

    @Override
    @Async
    public void undeployApiPlugin(String fabId, String apiId, String gwPluginId) throws CiaProcessorException {
        // Prepare Data
        ClientDeployPropertyFormat clientDeployPropertyFormat = coreProperties.getClientPropertiesByFab(fabId);

        for (ClientDeployProperty clientDeployProperty : clientDeployPropertyFormat.getDeployList()) {
            String url = buildUrl(IS_USE_HTTPS, clientDeployProperty.getClientDns(),
                    clientDeployProperty.getClientPort(), "/" + coreProperties.getClient().getDisplayName() +
                            CLIENT_ADMIN_RESTFUL_GW_PLUGIN_URI + "/" + apiId + "/" + fabId + "/" + gwPluginId);
            HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(clientDeployProperty.getAdminKey().get(0), null));

            // Send PATCH request
            ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.DELETE, url);
        }
    }

    @Data
    @AllArgsConstructor
    private static class ClientNodeInfo {
        private String url;
        private String clientDns;
        private String clientPort;
    }

}
