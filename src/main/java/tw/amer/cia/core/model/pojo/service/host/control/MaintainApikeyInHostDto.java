package tw.amer.cia.core.model.pojo.service.host.control;

import tw.amer.cia.core.model.pojo.service.common.apikey.SimplePermissionDto;
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
public class MaintainApikeyInHostDto implements WebApiTemplateInInterface, Serializable
{

    // General
    @NotBlank(message = "The MAINTAIN_ACTION must not be empty.")
    @JsonProperty("MAINTAIN_ACTION")
    private String maintainAction;

    @JsonProperty("ROLE_ID")
    private String roleId;

    @JsonProperty("DEPT_ID")
    private String deptId;

    @JsonProperty("FAB")
    private String fabId;

    @JsonProperty("PERMISSIONS")
    @Valid
    private List<SimplePermissionDto> permissions;

    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "The KEY_NAME must only contain alphanumeric characters, underscores, case sensitive.")
    @JsonProperty("KEY_NAME")
    private String keyName;

    @JsonProperty("APIKEY")
    private String apikeyId;

}
