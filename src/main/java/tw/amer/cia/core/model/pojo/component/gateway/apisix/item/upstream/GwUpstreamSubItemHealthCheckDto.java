package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.upstream;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GwUpstreamSubItemHealthCheckDto
{
    @Builder.Default
    @JsonProperty("http_statuses")
    private List<Integer> httpStatuses = Arrays.asList(
            200, 201, 202, 203, 204, 205, 206, 207, 208, 226, 300, 301, 302, 303, 304, 305, 306, 307, 308
    );

    @Builder.Default
    @JsonProperty("interval")
    private Integer interval = null;

    @Builder.Default
    @JsonProperty("successes")
    private Integer successes = 5;

}
