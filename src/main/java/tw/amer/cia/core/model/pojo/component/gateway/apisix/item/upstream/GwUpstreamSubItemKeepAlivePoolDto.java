package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.upstream;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GwUpstreamSubItemKeepAlivePoolDto
{
    @Builder.Default
    @JsonProperty("idle_timeout")
    private Integer idleTimeout = 60;

    @Builder.Default
    @JsonProperty("requests")
    private Integer requests = 1000;

    @Builder.Default
    @JsonProperty("size")
    private Integer size = 320;
}
