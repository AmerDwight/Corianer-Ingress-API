package tw.amer.cia.core.model.pojo.service.host.web.signOff.applyRoleAuthority;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_SignOffApplyRoleAuthorityInDto implements Serializable {

    @JsonProperty("ROLE_ID")
    @NotBlank(message = "ROLE_ID can not be empty.")
    private String roleId;

    @JsonProperty("APPLICANT_WORK_ID")
    @NotBlank(message = "APPLICANT_WORK_ID can not be empty.")
    private String applicantWorkId;

    @JsonProperty("APPLICANT_ACCOUNT_NAME")
    @NotBlank(message = "APPLICANT_ACCOUNT_NAME can not be empty.")
    private String applicantAccountName;

    @JsonProperty("APPLY_REASON")
    private String applyReason = "";

    @JsonProperty("APPLY_LIST")
    @NotEmpty(message = "APPLY_LIST can not be empty.")
    private List<Web_SignOffSystemCategoryForApplyRoleAuthorityDto> applyList;
}
