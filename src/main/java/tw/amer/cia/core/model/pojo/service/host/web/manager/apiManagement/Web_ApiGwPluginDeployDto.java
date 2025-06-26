package tw.amer.cia.core.model.pojo.service.host.web.manager.apiManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_ApiGwPluginDeployDto implements Serializable {
    @NotBlank(message = "The API_ID must not be empty.")
    @JsonProperty("API_ID")
    private String apiId;

    @JsonProperty("GW_PLUGIN_DEPLOYMENT")
    private Web_ApiDeployedBySiteDto gatewayPluginCards;
}
