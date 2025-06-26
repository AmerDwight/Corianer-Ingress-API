package tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_RoleManagementUserRoleDto implements Serializable {

    @JsonProperty("ROLE_ID")
    private String roleId;

    @JsonProperty("JOIN_TIME")
    private Instant joinTime;
}
