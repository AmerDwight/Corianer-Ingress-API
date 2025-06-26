package tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_RoleManagementCreateRoleInDto implements Serializable {
    @NotBlank(message = "ROLE_NAME is required.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "The ROLE_NAME must only contain alphanumeric characters, underscores, case sensitive.")
    @JsonProperty("ROLE_NAME")
    private String roleName;

    @NotBlank(message = "ROLE_TYPE is required.")
    @JsonProperty("ROLE_TYPE")
    private String roleType;

    @NotBlank(message = "ROLE_DESC is required.")
    @JsonProperty("ROLE_DESC")
    private String roleDesc;

    @JsonProperty("FAB_ID_APIS_MAP")
    Map<String, List<String>> apiListByFabIdMap;
}
