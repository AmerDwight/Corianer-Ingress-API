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
public class Web_SystemNewAvailableDeploymentDto implements Serializable {

    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @JsonProperty("AVAILABLE_REAL_FAB_LIST")
    private List<String> availableRealFabList;

    @JsonProperty("AVAILABLE_VIRTUAL_FAB_LIST")
    private List<String> availableVirtualFabList;

}