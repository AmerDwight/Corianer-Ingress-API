package tw.amer.cia.core.model.pojo.service.host.web.signOff.applyRoleAuthority;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyRoleAuthorityApplyItemDto {
    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @JsonProperty("API_ID")
    private String apiId;

    @JsonProperty("API_NAME")
    private String apiName;

    @JsonProperty("SCOPE_ID")
    private String fabId;
}
