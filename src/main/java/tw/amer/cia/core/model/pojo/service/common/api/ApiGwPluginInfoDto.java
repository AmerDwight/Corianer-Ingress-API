package tw.amer.cia.core.model.pojo.service.common.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiGwPluginInfoDto implements Serializable
{

    @JsonProperty("GW_PLUGIN_NAME")
    private String pluginName;

    @JsonProperty("GW_PLUGIN_TYPE")
    private String pluginType;

    @JsonProperty("GW_PLUGIN_PARAMETER")
    private String pluginParameter;
}
