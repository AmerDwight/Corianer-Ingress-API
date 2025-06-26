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
public class Web_RoleManagementMemberDto implements Serializable {
    @JsonProperty("USER_ID")
    private String userId;

    @JsonProperty("DEPT_CODE")
    private String deptCode;

    @JsonProperty("USER_NAME")
    private String userName;

    @JsonProperty("IS_UI_VISIBLE")
    private String isUiVisible;

}
