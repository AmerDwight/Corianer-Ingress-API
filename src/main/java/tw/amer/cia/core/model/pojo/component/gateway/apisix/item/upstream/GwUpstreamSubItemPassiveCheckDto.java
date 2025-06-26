package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.upstream;

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
public class GwUpstreamSubItemPassiveCheckDto implements Serializable
{
    @Builder.Default
    @JsonProperty("healthy")
    private GwUpstreamSubItemHealthCheckDto healthy = GwUpstreamSubItemHealthCheckDto.builder().build();

    @Builder.Default
    @JsonProperty("unhealthy")
    private GwUpstreamSubItemUnHealthCheckDto unhealthy = GwUpstreamSubItemUnHealthCheckDto.builder().build();

    @Builder.Default
    @JsonProperty("type")
    private String type = "http";
}
