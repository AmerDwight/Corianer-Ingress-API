package tw.amer.cia.core.component.functional.coriander;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.component.structural.annotation.ClientComponent;
import tw.amer.cia.core.component.structural.httpClient.HttpRequestSender;
import tw.amer.cia.core.model.database.*;
import tw.amer.cia.core.model.pojo.component.property.HostPropertyFormat;
import tw.amer.cia.core.model.pojo.service.common.AllProxyDataDto;
import tw.amer.cia.core.model.pojo.service.common.apikey.CompleteApikeyDto;
import tw.amer.cia.core.model.pojo.service.common.apikey.UpdateApikeyPermissionFromClientCompleteDto;
import tw.amer.cia.core.model.pojo.service.common.api.CompleteApiDto;
import tw.amer.cia.core.model.pojo.service.common.role.CompleteRoleDto;
import tw.amer.cia.core.model.pojo.service.common.system.CompleteSystemDto;

import javax.annotation.PostConstruct;
import java.net.ConnectException;
import java.util.*;

@Data
@Slf4j
@ConfigurationProperties(prefix = "deployment")
@ClientComponent
public class HttpCallHostApiComponent extends HttpRequestSender implements CallHostApiComponent {

    // Restful Criteria
    private final String RESTFUL_SEARCH_CRITERIA = "/criteria?";

    // Check Alive
    private final String CHECK_IDENTITY = "/identify";

    // Initial Complex Entity
    private final String RESTFUL_INITIAL_URI_SYSTEM = "/database/initial/system";
    private final String RESTFUL_INITIAL_URI_API = "/database/initial/api";
    private final String RESTFUL_INITIAL_URI_ROLE = "/database/initial/role";
    private final String RESTFUL_INITIAL_URI_ROLE_DEVICE = "/database/initial/role/device";
    private final String RESTFUL_INITIAL_URI_APIKEY = "/database/initial/apikey";
    private final String RESTFUL_INITIAL_URI_PROXY = "/database/initial/proxy";

    // Obtain Pure Table Data
    private final String RESTFUL_DB_URI_FAB = "/database/sync/FabEntity";
    private final String RESTFUL_DB_URI_GW_APIKEY = "/database/sync/GwApikeyEntity";
    private final String RESTFUL_DB_URI_GW_PLUGIN = "/database/sync/GwPluginEntity";
    private final String RESTFUL_DB_URI_EXTERNAL_SYSTEM_CONFIG = "/database/sync/ExternalSystemConfigEntity";

    // Apikey Manage
    private final String HOST_ADMIN_RESTFUL_APIKEY_URI = "/restful/admin/apikey";
    private final String HOST_ADMIN_RESTFUL_APIKEY_PERMISSION_URI = "/restful/admin/apikey/permission";

    private String deployType;
    private boolean IS_USE_HTTPS;

    public HttpCallHostApiComponent() {
        super(true);
    }

    @PostConstruct
    public void initHttpCallHostApiComponent() {
        this.deployType = coreProperties.getSetting().getDeployType();
        this.IS_USE_HTTPS = StringUtils.equalsIgnoreCase("HTTPS", coreProperties.getSetting().getScheme());
    }

    @Override
    public boolean checkHostAlive() {
        try {
            // Prepare Data
            HostPropertyFormat hostProperty = coreProperties.getHost();
            String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, hostProperty.getHostDns(), hostProperty.getHostPort(), CHECK_IDENTITY);
            HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(hostProperty.getAdminKey(), null));

            // Send GET request
            ResponseEntity<String> commandResult = sendHttpCommandWithFaultTolerance(entity, HttpMethod.GET, url);

            boolean isHostAlive = commandResult.getStatusCode().is2xxSuccessful();
            boolean isHostIdentified = commandResult.getBody().contains(GeneralSetting.CiaDeployType.HOST.getDisplayName().toLowerCase());

            boolean isHostAvailable = isHostAlive && isHostIdentified;
            if (!isHostAvailable) {
                throw new ConnectException();

            }
            return true;

        } catch (Exception e) {
            log.error("Get Exception: {}", e.getMessage());
            log.error("Exception cause: {}", e.getCause());
            log.error("Cannot approach host service.");
            log.error("Host info: {}:{} ", coreProperties.getHost().getHostDns(), coreProperties.getHost().getHostPort());
            return false;
        }
    }

    @Override
    public List<FabEntity> obtainFabDataByFabCollection(Collection<String> fabCollection) {
        // Prepare Criteria
        String criteriaBuilder = this.RESTFUL_SEARCH_CRITERIA + fabCriteriaBuilder(fabCollection);

        // Prepare Request
        HostPropertyFormat hostProperty = coreProperties.getHost();
        String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, hostProperty.getHostDns(),
                hostProperty.getHostPort(), RESTFUL_DB_URI_FAB + criteriaBuilder);
        HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(hostProperty.getAdminKey(), null));

        // Send GET request
        ResponseEntity<FabEntity[]> commandResult = sendHttpCommand(entity, HttpMethod.GET, url, FabEntity[].class);
        if (commandResult.getStatusCode().is2xxSuccessful()) {
            FabEntity[] data = commandResult.getBody();
            return Arrays.asList(data);
        }
        return null;
    }

    @Override
    public List<CompleteSystemDto> obtainCompleteSystemDataByFabCollection(Collection<String> fabCollection) {
        // Prepare Criteria
        String criteriaBuilder = this.RESTFUL_SEARCH_CRITERIA + fabCriteriaBuilder(fabCollection);

        // Prepare Request
        HostPropertyFormat hostProperty = coreProperties.getHost();
        String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, hostProperty.getHostDns(),
                hostProperty.getHostPort(), RESTFUL_INITIAL_URI_SYSTEM + criteriaBuilder);
        HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(hostProperty.getAdminKey(), null));

        // Send GET request
        ResponseEntity<CompleteSystemDto[]> commandResult = sendHttpCommand(entity, HttpMethod.GET, url, CompleteSystemDto[].class);
        if (commandResult.getStatusCode().is2xxSuccessful()) {
            CompleteSystemDto[] data = commandResult.getBody();
            return Arrays.asList(data);
        }
        return null;
    }

    @Override
    public List<GwPluginEntity> obtainAllGwPluginData() {
        // Prepare Request
        HostPropertyFormat hostProperty = coreProperties.getHost();
        String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, hostProperty.getHostDns(),
                hostProperty.getHostPort(), RESTFUL_DB_URI_GW_PLUGIN);
        HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(hostProperty.getAdminKey(), null));

        // Send GET request
        ResponseEntity<GwPluginEntity[]> commandResult = sendHttpCommand(entity, HttpMethod.GET, url, GwPluginEntity[].class);
        if (commandResult.getStatusCode().is2xxSuccessful()) {
            GwPluginEntity[] data = commandResult.getBody();
            return Arrays.asList(data);
        }
        return null;
    }


    @Override
    public List<ExternalSystemConfigEntity> obtainAllExtCtlEntity() {
        // Prepare Request
        HostPropertyFormat hostProperty = coreProperties.getHost();
        String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, hostProperty.getHostDns(),
                hostProperty.getHostPort(), RESTFUL_DB_URI_EXTERNAL_SYSTEM_CONFIG);
        HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(hostProperty.getAdminKey(), null));

        // Send GET request
        ResponseEntity<ExternalSystemConfigEntity[]> commandResult = sendHttpCommand(entity, HttpMethod.GET, url, ExternalSystemConfigEntity[].class);
        if (commandResult.getStatusCode().is2xxSuccessful()) {
            ExternalSystemConfigEntity[] data = commandResult.getBody();
            return Arrays.asList(data);
        }
        return null;
    }


    @Override
    public List<CompleteApiDto> obtainCompleteApiDataByFabCollection(Collection<String> fabCollection) {
        // Prepare Criteria
        String criteriaBuilder = this.RESTFUL_SEARCH_CRITERIA + fabCriteriaBuilder(fabCollection);

        // Prepare Request
        HostPropertyFormat hostProperty = coreProperties.getHost();
        String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, hostProperty.getHostDns(),
                hostProperty.getHostPort(), RESTFUL_INITIAL_URI_API + criteriaBuilder);
        HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(hostProperty.getAdminKey(), null));

        // Send GET request
        ResponseEntity<CompleteApiDto[]> commandResult = sendHttpCommand(entity, HttpMethod.GET, url, CompleteApiDto[].class);
        if (commandResult.getStatusCode().is2xxSuccessful()) {
            CompleteApiDto[] data = commandResult.getBody();
            return Arrays.asList(data);
        }
        return null;
    }

    @Override
    public List<CompleteRoleDto> obtainCompleteRoleDataByFabCollection(Collection<String> fabCollection) {
        // Prepare Criteria
        String criteriaBuilder = this.RESTFUL_SEARCH_CRITERIA + fabCriteriaBuilder(fabCollection);

        // Prepare Request
        HostPropertyFormat hostProperty = coreProperties.getHost();
        String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, hostProperty.getHostDns(),
                hostProperty.getHostPort(), RESTFUL_INITIAL_URI_ROLE + criteriaBuilder);
        HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(hostProperty.getAdminKey(), null));

        // Send GET request
        ResponseEntity<CompleteRoleDto[]> commandResult = sendHttpCommand(entity, HttpMethod.GET, url, CompleteRoleDto[].class);
        if (commandResult.getStatusCode().is2xxSuccessful()) {
            CompleteRoleDto[] data = commandResult.getBody();
            return Arrays.asList(data);
        }
        return null;
    }

    @Override
    public List<RoleDeviceEntity> obtainRoleDeviceDataByFabCollection(Collection<String> fabCollection) {
        // Prepare Criteria
        String criteriaBuilder = this.RESTFUL_SEARCH_CRITERIA + fabCriteriaBuilder(fabCollection);

        // Prepare Request
        HostPropertyFormat hostProperty = coreProperties.getHost();
        String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, hostProperty.getHostDns(),
                hostProperty.getHostPort(), RESTFUL_INITIAL_URI_ROLE_DEVICE + criteriaBuilder);
        HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(hostProperty.getAdminKey(), null));

        // Send GET request
        ResponseEntity<RoleDeviceEntity[]> commandResult = sendHttpCommand(entity, HttpMethod.GET, url, RoleDeviceEntity[].class);
        if (commandResult.getStatusCode().is2xxSuccessful()) {
            RoleDeviceEntity[] data = commandResult.getBody();
            return Arrays.asList(data);
        }
        return null;
    }

    @Override
    public List<CompleteApikeyDto> obtainCompleteApikeyDataByFabCollection(Collection<String> fabCollection) {
        // Prepare Criteria
        String criteriaBuilder = this.RESTFUL_SEARCH_CRITERIA + fabCriteriaBuilder(fabCollection);

        // Prepare Request
        HostPropertyFormat hostProperty = coreProperties.getHost();
        String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, hostProperty.getHostDns(),
                hostProperty.getHostPort(), "/" + RESTFUL_INITIAL_URI_APIKEY + criteriaBuilder);
        HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(hostProperty.getAdminKey(), null));

        // Send GET request
        ResponseEntity<CompleteApikeyDto[]> commandResult = sendHttpCommand(entity, HttpMethod.GET, url, CompleteApikeyDto[].class);
        if (commandResult.getStatusCode().is2xxSuccessful()) {
            CompleteApikeyDto[] data = commandResult.getBody();
            return Arrays.asList(data);
        }
        return null;
    }

    @Override
    public GwApikeyEntity obtainApikeyByApikeyId(String apikeyId) {
        // Prepare Request
        HostPropertyFormat hostProperty = coreProperties.getHost();
        String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, hostProperty.getHostDns(),
                hostProperty.getHostPort(), RESTFUL_DB_URI_GW_APIKEY + "/" + apikeyId);
        HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(hostProperty.getAdminKey(), null));

        // Send GET request
        ResponseEntity<GwApikeyEntity> commandResult = sendHttpCommand(entity, HttpMethod.GET, url, GwApikeyEntity.class);
        if (commandResult.getStatusCode().is2xxSuccessful()) {
            return commandResult.getBody();
        }
        return null;
    }

    @Override
    public AllProxyDataDto obtainAllProxyData() {
        // Prepare Request
        HostPropertyFormat hostProperty = coreProperties.getHost();
        String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, hostProperty.getHostDns(),
                hostProperty.getHostPort(), RESTFUL_INITIAL_URI_PROXY);
        HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(hostProperty.getAdminKey(), null));

        // Send GET request
        ResponseEntity<AllProxyDataDto> commandResult = sendHttpCommand(entity, HttpMethod.GET, url, AllProxyDataDto.class);
        if (commandResult.getStatusCode().is2xxSuccessful()) {
            AllProxyDataDto data = commandResult.getBody();
            return data;
        }
        return null;
    }


    @Override
    public boolean createOrUpdateApikeyFromClient(GwApikeyEntity newApikey) {
        // Prepare Data
        HostPropertyFormat hostProperty = coreProperties.getHost();
        String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, hostProperty.getHostDns(),
                hostProperty.getHostPort(), coreProperties.getHost().getDisplayName() +
                        HOST_ADMIN_RESTFUL_APIKEY_URI);
        HttpEntity<Object> entity = new HttpEntity<>(newApikey, apiControlHeader(hostProperty.getAdminKey(), null));

        // Send PUT request
        ResponseEntity<String> commandResult = sendHttpCommand(entity, HttpMethod.POST, url);

        return commandResult.getStatusCode().is2xxSuccessful();
    }

    @Override
    public boolean updateApikeyPermissionBatchFromClient(String apikeyId, List<GwApikeyPermissionEntity> revokePermissionList, List<GwApikeyPermissionEntity> grantPermissionList) {
        boolean procedureSuccess = true;

        // Prepare Data
        UpdateApikeyPermissionFromClientCompleteDto updateDataDto =
                UpdateApikeyPermissionFromClientCompleteDto.builder()
                        .revokePermissionList(revokePermissionList)
                        .grantPermissionList(grantPermissionList)
                        .build();
        HostPropertyFormat hostProperty = coreProperties.getHost();
        String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, hostProperty.getHostDns(),
                hostProperty.getHostPort(), coreProperties.getHost().getDisplayName() +
                        HOST_ADMIN_RESTFUL_APIKEY_PERMISSION_URI + "/" + apikeyId);

        HttpEntity<Object> entity = new HttpEntity<>(updateDataDto, apiControlHeader(hostProperty.getAdminKey(), null));

        ResponseEntity<String> commandResult = sendHttpCommand(entity, HttpMethod.PUT, url);
        procedureSuccess &= commandResult.getStatusCode().is2xxSuccessful();

        return (procedureSuccess);
    }

    @Override
    public boolean deleteApikeyCheckFromClient(String apikeyId) {
        // Prepare Data
        HostPropertyFormat hostProperty = coreProperties.getHost();
        String url = HttpCallHostApiComponent.buildUrl(IS_USE_HTTPS, hostProperty.getHostDns(),
                hostProperty.getHostPort(), "/" + coreProperties.getHost().getDisplayName() + HOST_ADMIN_RESTFUL_APIKEY_URI + "/" + apikeyId);
        HttpEntity<Object> entity = new HttpEntity<>("", apiControlHeader(hostProperty.getAdminKey(), null));

        // Send DELETE request
        ResponseEntity<String> commandResult = sendHttpCommand(entity, HttpMethod.DELETE, url);

        return commandResult.getStatusCode().is2xxSuccessful();
    }

    private String fabCriteriaBuilder(Collection<String> fabCollection) {
        StringBuilder stringBuilder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(fabCollection)) {
            Set<String> fabIdSet = new HashSet<>(fabCollection);
            stringBuilder.append("fab=");
            stringBuilder.append(StringUtils.join(fabIdSet, ","));
        }

        return stringBuilder.toString();
    }
}
