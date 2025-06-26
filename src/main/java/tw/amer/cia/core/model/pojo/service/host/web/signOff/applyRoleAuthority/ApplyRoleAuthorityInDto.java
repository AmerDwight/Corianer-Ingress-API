package tw.amer.cia.core.model.pojo.service.host.web.signOff.applyRoleAuthority;

import tw.amer.cia.core.model.database.ApiEntity;
import tw.amer.cia.core.model.database.SystemEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyRoleAuthorityInDto implements Serializable {

    private String roleId;
    private String applicantId;
    private String applicantAccount;
    private String applyReason;
    private Map<String, SystemEntity> applySystemBySystemIdMap;
    private Map<String, ApiEntity> applyApiByApiIdMap;
    private List<ApplyRoleAuthorityApplyItemDto> onApplyItemList;
    private Map<String, List<ApplyRoleAuthorityApplyItemDto>> applyItemBySystemIdMap;
    private Map<String, List<String>> fabIdListByApiIdMap;

}
