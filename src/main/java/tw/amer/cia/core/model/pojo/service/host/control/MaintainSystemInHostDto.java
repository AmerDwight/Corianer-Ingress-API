package tw.amer.cia.core.model.pojo.service.host.control;

import tw.amer.cia.core.model.pojo.service.common.system.SystemDeploymentDto;
import tw.amer.cia.core.model.pojo.service.host.control.marker.WebApiTemplateInInterface;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintainSystemInHostDto implements WebApiTemplateInInterface, Serializable
{
    @NotBlank(message = "The MAINTAIN_ACTION must not be empty.")
    @JsonProperty("MAINTAIN_ACTION")
    private String maintainAction;

    @NotBlank(message = "The SYSTEM_NAME must not be empty.")
    @Pattern(regexp = "^[\\w-]+$", message = "The SYSTEM_NAME must contain only alphanumeric characters and underscores, case sensitive.")
    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @JsonProperty("SYSTEM_ENG_NAME")
    private String systemEngName;

    @JsonProperty("SERVICE_LEVEL")
    private Integer serviceLevel;

    @JsonProperty("SYSTEM_DESC")
    private String systemDesc;

    @JsonProperty("OWNER")
    private String owner;

    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    @Valid
    @JsonProperty("DEPLOYMENT")
    private List<SystemDeploymentDto> deployment;

}
