package tw.amer.cia.core.model.pojo.service.host.web.info.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.amer.cia.core.model.pojo.service.host.web.item.Web_BasicSiteItemDto;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_DetailSystemInfoOutDto implements Serializable {

    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @JsonProperty("SYSTEM_ENG_NAME")
    private String systemEngName;

    @JsonProperty("SYSTEM_DESC")
    private String systemDesc;

    @JsonProperty("SERVICE_LEVEL")
    private Integer serviceLevel;

    @JsonProperty("OWNER")
    private String owner;

    @JsonProperty("OWNER_NAME")
    private String ownerName;

    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    @JsonProperty("APPLICABLE_FLAG")
    private String applicableFlag;

    @JsonProperty("CREATE_TIME")
    private Instant createTime;

    @JsonProperty("LM_TIME")
    private Instant lmTime;

    @JsonProperty("DEPLOYED_SITE_LIST")
    private List<Web_BasicSiteItemDto> siteList;
}
