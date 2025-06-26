package tw.amer.cia.core.model.pojo.service.host.control;

import tw.amer.cia.core.model.pojo.service.host.control.marker.WebApiTemplateOutDto;
import tw.amer.cia.core.model.pojo.service.common.api.ApiDeployedFabDto;
import tw.amer.cia.core.model.pojo.service.common.api.ApiEndpointDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder
public class MaintainRetrieveApiOutHostDto extends WebApiTemplateOutDto implements Serializable
{

    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @JsonProperty("API_NAME")
    private String apiName;

    @JsonProperty("API_TYPE")
    private String apiType;

    @JsonProperty("APPLICABLE_FLAG")
    private String applicableFlag;

    @JsonProperty("OWNER")
    private String owner;

    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    @JsonProperty("ENDPOINT")
    private List<ApiEndpointDto> endpoint;

    @JsonProperty("DEPLOYMENT")
    private List<ApiDeployedFabDto> deployment;
}
