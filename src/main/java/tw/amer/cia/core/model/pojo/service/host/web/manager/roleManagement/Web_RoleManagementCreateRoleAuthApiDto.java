package tw.amer.cia.core.model.pojo.service.host.web.manager.roleManagement;

import tw.amer.cia.core.model.pojo.service.host.web.item.Web_BasicSiteItemAuthedDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.List;

@Data
@FieldNameConstants
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_RoleManagementCreateRoleAuthApiDto implements Serializable {
    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @JsonProperty("API_ID")
    private String apiId;

    @JsonProperty("API_NAME")
    private String apiName;

    @JsonProperty("DEPLOYED_SITE_LIST")
    private List<Web_BasicSiteItemAuthedDto> siteAuthedList;
}
