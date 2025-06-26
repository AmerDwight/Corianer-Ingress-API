package tw.amer.cia.core.model.pojo.service.host.web.signOff.applyRoleAuthority;

import tw.amer.cia.core.model.pojo.service.host.web.signOff.SignatureReviewer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyRoleAuthoritySingleSystemCompleteDataDto implements Serializable {
    @NotBlank
    private String systemId;

    @NotBlank
    private String systemName;

    @NotBlank
    private String applicantWorkId;

    @NotBlank
    private String applicantAccount;

    @NotBlank
    private String applyReason;

    @NotEmpty
    Set<String> applyScopes;

    @NotEmpty
    List<ApplyRoleAuthorityApiApplyDataWithNameDto> applyItems;

    @NotNull
    private Map<Integer, SignatureReviewer> reviewerBySeqMap;
}
