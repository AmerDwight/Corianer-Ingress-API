package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.upstream;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GwUpstreamSubItemUnHealthCheckDto implements Serializable
{
    @Builder.Default
    @JsonProperty("http_statuses")
    private List<Integer> httpStatuses = Arrays.asList(
            429, 500, 503
    );

    @Builder.Default
    @JsonProperty("interval")
    private Integer interval = null;

    @Builder.Default
    @JsonProperty("http_failures")
    private Integer httpFailures = 2;

    @Builder.Default
    @JsonProperty("tcp_failures")
    private Integer tcpFailures = 2;

    @Builder.Default
    @JsonProperty("timeouts")
    private Integer timeouts = 7;
}
