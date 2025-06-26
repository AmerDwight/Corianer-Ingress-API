package tw.amer.cia.core.model.pojo.service.host.web.signOff.applyRoleAuthority;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyRoleAuthorityApiDeployDataForDto implements Serializable {
    private String apiId;
    private String fabId;
}
