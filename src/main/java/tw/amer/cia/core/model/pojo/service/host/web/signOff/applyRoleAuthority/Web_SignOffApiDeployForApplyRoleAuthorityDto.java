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
public class Web_SignOffApiDeployForApplyRoleAuthorityDto implements Serializable {
    @JsonProperty("API_ID")
    private String apiId;
    @JsonProperty("SCOPE_ID_LIST")
    private List<String> scopeIdList;
}
