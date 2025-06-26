package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route;

import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.plugin.RoutePluginsDto;
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
public class GwRouteRetrieveValueTagDto implements Serializable
{
    @JsonProperty("plugins")
    private RoutePluginsDto plugins;
}
