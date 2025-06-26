package tw.amer.cia.core.model.pojo.service.host.web.signOff.applyRoleAuthority;


import tw.amer.cia.core.model.pojo.service.host.web.signOff.marker.SignOffProcessComponentApplyItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyRoleAuthorityApiApplyDataWithNameDto implements SignOffProcessComponentApplyItem, Serializable {
    private String apiId;
    private String apiName;
    private String fabId;

    @Override
    public String getApplyItemCompleteDescribe() {
        StringBuilder sb = new StringBuilder();
        sb.append("Apply Item :");
        sb.append(" API Name: " + StringUtils.defaultString(this.apiName,"Unknown"));
        sb.append("  Effective Scope: " + StringUtils.defaultString(this.fabId,"Unknown"));
        return sb.toString();
    }

    @Override
    public String getApplyItemSimpleDescribe() {
        StringBuilder sb = new StringBuilder();
        sb.append(" API: " + StringUtils.defaultString(this.apiName,"Unknown"));
        return sb.toString();
    }
}
