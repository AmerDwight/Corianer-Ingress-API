package tw.amer.cia.core.model.pojo.component.gateway.apisix.item;

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
public class MetaDto implements Serializable
{
    @JsonProperty("disable")
    @Builder.Default
    private boolean disable = false;
}