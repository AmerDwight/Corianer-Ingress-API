package tw.amer.cia.core.model.pojo.service.host.web.manager.realtimeGateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.net.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_RealtimeGatewayGrafanaUrlPathDto implements Serializable {

    @JsonProperty("FAB_ID")
    private String fabId;

    @JsonProperty("URL")
    private URL url;

}
