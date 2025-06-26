package tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_RoleManagementPanelDataDto implements Serializable {
    @JsonProperty("ROLE_ID")
    private String roleId;

    @JsonProperty("ROLE_TYPE")
    private String roleType;

    @JsonProperty("ROLE_NAME")
    private String roleName;

    @JsonProperty("ROLE_DESC")
    private String roleDesc;

}
