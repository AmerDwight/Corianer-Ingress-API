package tw.amer.cia.core.model.pojo.service.host.web.apikeyManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_ApiEndpointDataDetailDto implements Serializable {
    @JsonProperty("IS_PRODUCTION")
    private boolean isProduction;

    @JsonProperty("API_ITF_TYPE")
    private String apiItfType;

    @JsonProperty("API_GW_ROUTE")
    private String apiGwRoute;

    @JsonProperty("API_ACCEPT_METHOD")
    private List<String> apiAcceptMethodList;
}
