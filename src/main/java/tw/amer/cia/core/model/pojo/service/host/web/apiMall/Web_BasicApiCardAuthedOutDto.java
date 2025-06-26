package tw.amer.cia.core.model.pojo.service.host.web.apiMall;

import tw.amer.cia.core.common.WebConstantLib;
import tw.amer.cia.core.model.pojo.service.host.web.item.Web_BasicSiteItemAuthedDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_BasicApiCardAuthedOutDto implements Serializable {

    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @JsonProperty("API_ID")
    private String apiId;

    @JsonProperty("API_NAME")
    private String apiName;

    @JsonProperty("API_DESC")
    private String apiDesc;

    @JsonProperty("API_TYPE")
    private String apiType;

    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    @JsonProperty("APPLICABLE_FLAG")
    private String applicableFlag;

    @JsonProperty("ROLE_AUTHED_STATUS")
    private WebConstantLib.WEB_UI_API_CARD_ROLE_AUTHED_STATUS roleAuthedStatus;

    @JsonProperty("OWNER")
    private String owner;

    @JsonProperty("CREATE_TIME")
    @CreatedDate
    private Instant createTime;

    @JsonProperty("DEPLOYED_SITE_LIST")
    private List<Web_BasicSiteItemAuthedDto> siteAuthedList;
}
