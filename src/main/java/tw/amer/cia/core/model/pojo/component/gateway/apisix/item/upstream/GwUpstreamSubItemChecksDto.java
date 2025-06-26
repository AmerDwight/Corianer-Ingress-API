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
public class GwUpstreamSubItemChecksDto
{
    @Builder.Default
    @JsonProperty("passive")
    private GwUpstreamSubItemPassiveCheckDto passive = GwUpstreamSubItemPassiveCheckDto.builder().build();

    @JsonProperty("active")
    private GwUpstreamSubItemActiveCheckDto active;
}
