package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.upstream;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GwUpstreamSubItemTimeoutDto implements Serializable
{
    @Builder.Default
    @JsonProperty("connect")
    private Integer connect = 6;

    @Builder.Default
    @JsonProperty("send")
    private Integer send = 6;

    @Builder.Default
    @JsonProperty("read")
    private Integer read = 6;
}
