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
public class Web_GatewayPluginOverviewCardDto implements Serializable {
    @NotBlank(message = "The GW_PLUGIN_ID must not be empty.")
    @JsonProperty("GW_PLUGIN_ID")
    private String gwPluginId;

    @JsonProperty("GW_PLUGIN_NAME")
    private String gwPluginName;

    @JsonProperty("GW_PLUGIN_DESC")
    private String gwPluginDesc;

    @JsonProperty("ADOPTION_COUNT")
    private Integer adoptionCount;

    @JsonProperty("ALL_FAB_COUNT")
    private Integer fabCount;
}
