package tw.amer.cia.core.model.pojo.service.host.web.manager.systemManagement;


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
public class Web_SystemDeployOverviewDto implements Serializable {
    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @JsonProperty("OVERVIEW_STATUS")
    private Web_SystemDeployOverviewStatusDto overviewStatus;

    @JsonProperty("SITE_LIST")
    private List<Web_SystemDeployBySiteDto> deployBySiteList;

}