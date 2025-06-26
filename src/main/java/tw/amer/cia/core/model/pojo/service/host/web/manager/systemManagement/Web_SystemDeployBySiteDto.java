package tw.amer.cia.core.model.pojo.service.host.web.manager.systemManagement;


import tw.amer.cia.core.model.pojo.service.common.system.SystemDeploymentDto;
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
public class Web_SystemDeployBySiteDto implements Serializable {
    @JsonProperty("SITE")
    private String site;

    @JsonProperty("DEPLOYMENT")
    private List<SystemDeploymentDto> deploymentList;

}