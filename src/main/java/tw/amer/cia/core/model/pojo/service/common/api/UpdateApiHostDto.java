package tw.amer.cia.core.model.pojo.service.common.api;

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
public class UpdateApiHostDto implements Serializable
{
    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @JsonProperty("API_NAME")
    private String apiName;

    @JsonProperty("API_ENG_NAME")
    private String apiEngName;

    @JsonProperty("API_TYPE")
    private String apiType;

    @JsonProperty("APPLICABLE_FLAG")
    private String applicableFlag;

    @JsonProperty("API_DESC")
    private String apiDesc;

    @JsonProperty("OWNER")
    private String owner;

    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    @JsonProperty("ENDPOINT")
    private List<ApiEndpointDto> endpoint;

    @JsonProperty("DEPLOYMENT")
    private List<ApiDeployedFabDto> deployment;

}
