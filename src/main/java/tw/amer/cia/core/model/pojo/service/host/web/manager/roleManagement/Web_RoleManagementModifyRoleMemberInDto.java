package tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_RoleManagementModifyRoleMemberInDto implements Serializable {
    @NotBlank(message = "ROLE_ID is required.")
    @JsonProperty("ROLE_ID")
    private String roleId;

    @JsonProperty("REMOVE_USER_ID_LIST")
    private List<String> removeUserIdList;

    @JsonProperty("ADD_USER_ID_LIST")
    private List<String> addUserIdList;
}
