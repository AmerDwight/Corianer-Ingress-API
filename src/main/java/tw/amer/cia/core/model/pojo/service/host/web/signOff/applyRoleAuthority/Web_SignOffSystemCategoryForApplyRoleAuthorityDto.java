package tw.amer.cia.core.model.pojo.service.host.web.signOff.applyRoleAuthority;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_SignOffSystemCategoryForApplyRoleAuthorityDto implements Serializable {
    @JsonProperty("SYSTEM_ID")
    private String systemId;
    @JsonProperty("API_LIST")
    private List<Web_SignOffApiDeployForApplyRoleAuthorityDto> onApplyApiLIst;
}
