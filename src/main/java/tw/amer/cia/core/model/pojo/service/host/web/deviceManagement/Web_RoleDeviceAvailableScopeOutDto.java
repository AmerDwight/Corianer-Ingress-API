package tw.amer.cia.core.model.pojo.service.host.web.deviceManagement;

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
public class Web_RoleDeviceAvailableScopeOutDto implements Serializable {

    @JsonProperty("ROLE_ID")
    private String roleId;

    @JsonProperty("AVAILABLE_FAB_LIST")
    private List<String> availableFabList;
}
