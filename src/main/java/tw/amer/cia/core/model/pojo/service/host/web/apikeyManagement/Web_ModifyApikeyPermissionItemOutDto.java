package tw.amer.cia.core.model.pojo.service.host.web.apikeyManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_ModifyApikeyPermissionItemOutDto implements Serializable {

    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @JsonProperty("API_ID")
    private String apiId;

    @JsonProperty("API_NAME")
    private String apiName;

    @JsonProperty("TOTAL_AUTHORIZED_SCOPES")
    private List<String> totalAuthorizedScopes;

    @JsonProperty("SELECTED_SCOPES")
    private List<String> selectedScopes;

    @JsonProperty("API_GATEWAY_ENDPOINT_LIST")
    private Map<String, List<Web_ApiEndpointDataDetailDto>> apiGatewayEndpointListByFabId;
}
