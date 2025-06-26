package tw.amer.cia.core.model.pojo.service.host.web.info.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.amer.cia.core.model.pojo.service.host.web.item.Web_BasicSiteItemAuthedDto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_DetailApiInfoOutDto implements Serializable {

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

    @JsonProperty("CREATE_TIME")
    private Instant createTime;

    @JsonProperty("LM_TIME")
    private Instant lmTime;

    @JsonProperty("DEPLOYED_SITE_LIST")
    private List<Web_BasicSiteItemAuthedDto> siteAuthedList;
}
