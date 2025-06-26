package tw.amer.cia.core.model.pojo.service.host.web.manager.systemManagement;

import tw.amer.cia.core.model.pojo.service.common.system.SystemDeploymentDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_UpdateSystemDeploymentDto implements Serializable {

    @NotBlank(message = "The SYSTEM_ID must not be empty.")
    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @JsonProperty("DEPLOYMENT")
    private List<SystemDeploymentDto> deployment;
}