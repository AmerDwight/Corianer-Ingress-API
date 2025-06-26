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
public class Web_SignOffApplyRoleAuthorityOutDto implements Serializable {
    @JsonProperty("ROLE_ID")
    private String roleId;

    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @JsonProperty("APPLY_FORM_STATUS")
    private String applyFormStatus;

    @JsonProperty("APPLY_FORM_NUMBER")
    private String applyFormNumber;

    @JsonProperty("APPLY_FORM_MESSAGE")
    private String applyFormMessage;

    @JsonProperty("API_LIST")
    private List<ApplyRoleAuthorityApplyItemDto> onApplyApiLIst;
}
