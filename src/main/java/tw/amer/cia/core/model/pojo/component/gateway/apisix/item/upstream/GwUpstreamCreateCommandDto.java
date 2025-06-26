package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.upstream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GwUpstreamCreateCommandDto
{

    @Builder.Default
    @JsonProperty("nodes")
    private List<GwUpstreamSubItemNodeDto> nodes = null;

    @Builder.Default
    @JsonProperty("checks")
    private GwUpstreamSubItemChecksDto checks = null;

    @Builder.Default
    @JsonProperty("keepalive_pool")
    private GwUpstreamSubItemKeepAlivePoolDto keepalivePool = GwUpstreamSubItemKeepAlivePoolDto.builder().build();

    @Builder.Default
    @JsonProperty("type")
    private String type = "roundrobin";

    @Builder.Default
    @JsonProperty("scheme")
    private String scheme = "http";

    @Builder.Default
    @JsonProperty("timeout")
    private GwUpstreamSubItemTimeoutDto timeout = new GwUpstreamSubItemTimeoutDto(6, 6, 6);

    @JsonProperty("name")
    private String name;

    @JsonProperty("id")
    private String id;

}
