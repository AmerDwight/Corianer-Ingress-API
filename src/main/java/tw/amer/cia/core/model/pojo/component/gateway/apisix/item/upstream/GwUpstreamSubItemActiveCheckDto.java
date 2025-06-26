package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.upstream;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GwUpstreamSubItemActiveCheckDto
{
    @Builder.Default
    @JsonProperty("healthy")
    private GwUpstreamSubItemHealthCheckDto healthy = GwUpstreamSubItemHealthCheckDto.builder()
            .httpStatuses(Arrays.asList(200, 302))
            .successes(2)
            .interval(1)
            .build();

    @Builder.Default
    @JsonProperty("timeout")
    private int timeout = 1;

    @JsonProperty("http_path")
    private String httpPath;

    @Builder.Default
    @JsonProperty("unhealthy")
    private GwUpstreamSubItemUnHealthCheckDto unhealthy = GwUpstreamSubItemUnHealthCheckDto.builder()
            .httpStatuses(Arrays.asList(429, 404, 500, 501, 502, 503, 504, 505))
            .httpFailures(3)
            .tcpFailures(2)
            .timeouts(3)
            .interval(1)
            .build();

    @Builder.Default
    @JsonProperty("concurrency")
    private int concurrency = 10;

    @Builder.Default
    @JsonProperty("type")
    private String type = "http";
}
