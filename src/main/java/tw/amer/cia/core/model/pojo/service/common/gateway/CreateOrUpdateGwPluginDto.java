package tw.amer.cia.core.model.pojo.service.common.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrUpdateGwPluginDto implements Serializable
{

    @JsonProperty("GW_PLUGIN_ID")
    private String gwPluginId;

    @JsonProperty("GW_PLUGIN_TYPE")
    private String gwPluginType;

    @JsonProperty("GW_PLUGIN_NAME")
    private String gwPluginName;

    @JsonProperty("GW_PLUGIN_DEPLOY")
    private String gwPluginDeploy;

    @JsonProperty("GW_PLUGIN_TEMPLATE")
    private String gwPluginTemplate;

    @JsonProperty("GW_PLUGIN_SAMPLE")
    private String gwPluginSample;
}
