package tw.amer.cia.core.model.pojo.service.common.gateway;

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
public class ExternalGatewayInfoDto implements Serializable {

    @JsonProperty("ENABLE_HTTPS")
    private boolean enableHttps;

    @JsonProperty("EXTERNAL_GATEWAY_HOST")
    private String extGatewayHost;

    @JsonProperty("EXTERNAL_GATEWAY_PORT")
    @Builder.Default
    private Integer extGatewayPort = 8080;

    @JsonProperty("EXTERNAL_GRAFANA_HOST")
    private String extGrafanaHost;

    @JsonProperty("EXTERNAL_GRAFANA_PORT")
    @Builder.Default
    private Integer extGrafanaPort = 8655;

    @JsonProperty("FAB_LIST")
    private List<String> fabList;
}
