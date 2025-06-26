package tw.amer.cia.core.model.pojo.service.common.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeployApiGwPluginDto implements Serializable
{

    @JsonProperty("API_ID")
    private String apiId;

    @JsonProperty("FAB_ID")
    private String fabId;

    @JsonProperty("GW_PLUGIN_ID")
    private String gwPluginId;

    // NOTE: It must be Key-Value Pair for Gateway Config
    @JsonProperty("GW_PLUGIN_PARAMETER_MAP")
    private Map<String,String> gwPluginParameterMap;
}
