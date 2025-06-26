package tw.amer.cia.core.model.pojo.service.host.web.signOff.applyRoleAuthority;

import tw.amer.cia.core.model.pojo.service.host.web.signOff.marker.ApplyRoleAuthorityRequestContext;
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
public class ApplyRoleAuthorityOnSendSignOffDto implements Serializable {
    private String roleId;
    private String systemId;
    private String systemName;
    private String applicantId;
    private List<ApplyRoleAuthorityApplyItemDto> onApplyItemList;
    private ApplyRoleAuthorityRequestContext onSendContext;
}
