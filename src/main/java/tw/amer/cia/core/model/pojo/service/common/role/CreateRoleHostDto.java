package tw.amer.cia.core.model.pojo.service.common.role;


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
public class CreateRoleHostDto implements Serializable
{
    @JsonProperty("ROLE_ID")
    private String roleId;

    @JsonProperty("ROLE_NAME")
    private String roleName;

    @JsonProperty("ROLE_TYPE")
    private String roleType;

    @JsonProperty("ROLE_DESC")
    private String roleDesc;

    // TODO Check
    @JsonProperty("UPDATE_REF")
    @Builder.Default
    private String updateRef = "CIA.INIT";

    @JsonProperty("FAB")
    private String fabId;

    @JsonProperty("API_ID_LIST")
    private List<String> apiIdList;
}
