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
public class GwUpstreamSubItemNodeDto
{
    @JsonProperty("host")
    private String host;

    @JsonProperty("port")
    private int port;

    @Builder.Default
    @JsonProperty("weight")
    private int weight = 1;
}
