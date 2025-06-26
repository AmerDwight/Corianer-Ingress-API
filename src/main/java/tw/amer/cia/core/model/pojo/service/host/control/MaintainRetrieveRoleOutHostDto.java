package tw.amer.cia.core.model.pojo.service.host.control;

import tw.amer.cia.core.model.pojo.service.common.role.RoleFabScopeDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintainRetrieveRoleOutHostDto
{
    @JsonProperty("ROLE_ID")
    private String roleId;

    @JsonProperty("ROLE_NAME")
    private String roleName;

    @JsonProperty("ROLE_TYPE")
    private String roleType;

    @JsonProperty("ROLE_DESC")
    private String roleDesc;

    @JsonProperty("FAB_SCOPE")
    private List<RoleFabScopeDto> fabScope;
}
