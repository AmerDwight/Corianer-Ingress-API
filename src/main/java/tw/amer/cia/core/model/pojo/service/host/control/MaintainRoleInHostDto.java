package tw.amer.cia.core.model.pojo.service.host.control;

import tw.amer.cia.core.model.pojo.service.common.role.SimpleAuthorityDto;
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
public class MaintainRoleInHostDto implements WebApiTemplateInInterface, Serializable
{
    @NotBlank(message = "The MAINTAIN_ACTION must not be empty.")
    @JsonProperty("MAINTAIN_ACTION")
    private String maintainAction;

    @NotBlank(message = "The ROLE_ID must not be empty.")
    @JsonProperty("ROLE_ID")
    private String roleId;

    @JsonProperty("ROLE_NAME")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "The ROLE_NAME must only contain alphanumeric characters, underscores, case sensitive.")
    private String roleName;

    @JsonProperty("ROLE_TYPE")
    private String roleType;

    @JsonProperty("ROLE_DESC")
    private String roleDesc;

    @JsonProperty("FAB")
    private String fabId;

    @JsonProperty("AUTHORITIES")
    @Valid
    private List<SimpleAuthorityDto> authorities;
}
