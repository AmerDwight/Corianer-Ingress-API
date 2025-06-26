package tw.amer.cia.core.model.pojo.service.host.control;

import tw.amer.cia.core.model.pojo.service.common.api.ApiDeployedFabDto;
import tw.amer.cia.core.model.pojo.service.host.control.marker.WebApiTemplateInInterface;
import tw.amer.cia.core.model.pojo.service.common.api.ApiEndpointDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintainApiInHostDto implements WebApiTemplateInInterface, Serializable
{
    @NotBlank(message = "The MAINTAIN_ACTION must not be empty.")
    @JsonProperty("MAINTAIN_ACTION")
    private String maintainAction;

    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @JsonProperty("API_NAME")
    private String apiName;

    @JsonProperty("API_ENG_NAME")
    private String apiEngName;

    @JsonProperty("API_DESC")
    private String apiDesc;

    @JsonProperty("API_TYPE")
    private String apiType;

    @JsonProperty("APPLICABLE_FLAG")
    private String applicableFlag;

    @JsonProperty("OWNER")
    private String owner;

    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    @JsonProperty("ENDPOINT")
    @Valid
    private List<ApiEndpointDto> endpoint;

    @Valid
    @JsonProperty("DEPLOYMENT")
    private List<ApiDeployedFabDto> deployment;

}
