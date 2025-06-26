package tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_RoleManagementUserRoleListDto implements Serializable {

    @JsonProperty("USER_ID")
    private String userId;

    @JsonProperty("ROLE_LIST")
    private Collection<Web_RoleManagementUserRoleDto> roles;

}
