package tw.amer.cia.core.component.functional.signOff;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.pojo.service.host.web.signOff.applyRoleAuthority.ApplyRoleAuthoritySingleSystemCompleteDataDto;
import tw.amer.cia.core.model.pojo.service.host.web.signOff.marker.ApplyRoleAuthorityRequestContext;
import tw.amer.cia.core.model.pojo.service.host.web.signOff.marker.ApplyRoleAuthorityResponseContext;
import tw.amer.cia.core.model.pojo.service.host.web.signOff.marker.SignOffProcessComponentApplyItem;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface SignOffProcessComponent {
    // Apply Role Authority
    // 生成ApplyRoleAuthorityContext
    ApplyRoleAuthorityRequestContext createApplyRoleAuthorityContext(ApplyRoleAuthoritySingleSystemCompleteDataDto dto);
    ApplyRoleAuthorityResponseContext sendApplyRoleAuthoritySignature(ApplyRoleAuthorityRequestContext applyContext) throws DataSourceAccessException;

    default String provideApplyDescribe(@NotNull String applyType,
                                        @NotEmpty List<String> scopeList,
                                        @NotEmpty List<SignOffProcessComponentApplyItem> applyDataList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ CIA API權限申請 ] - ");
        stringBuilder.append("申請範圍： ");
        for (int i = 0; i < scopeList.size(); i++) {
            if (i == 0) {
                stringBuilder.append(scopeList.get(i));
            }else{
                stringBuilder.append(" ,").append(scopeList.get(i));
            }
        }
        stringBuilder.append(" -- ");

        stringBuilder.append("申請清單：   ");
        for(SignOffProcessComponentApplyItem applyItem: applyDataList){
            stringBuilder.append(" [ ");
            stringBuilder.append(applyItem.getApplyItemSimpleDescribe()).append(" ] ");
        }
        stringBuilder.append(" -- ").append(" 申請說明： ");
        return stringBuilder.toString();
    }
}
