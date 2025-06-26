package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GwRouteRetrieveResponseDto implements Serializable
{
    @JsonProperty("createdIndex")
    private Integer createdIndex;

    @JsonProperty("key")
    private String key;

    @JsonProperty("modifiedIndex")
    private Integer modifiedIndex;

    @JsonProperty("value")
    private GwRouteRetrieveValueTagDto value;

}
