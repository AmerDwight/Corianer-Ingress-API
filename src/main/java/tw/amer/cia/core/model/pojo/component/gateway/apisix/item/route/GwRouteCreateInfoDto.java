package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route;

import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.plugin.RoutePluginsDto;
import tw.amer.cia.core.common.GeneralSetting;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GwRouteCreateInfoDto implements Serializable
{
    @JsonProperty("uri")
    private String uri;

    @JsonProperty("name")
    private String name;

    @JsonProperty("methods")
    @Builder.Default
    private List<String> methods = GeneralSetting.getDefaultHttpMethods();

    @JsonProperty("plugins")
    private RoutePluginsDto plugins;

    @JsonProperty("upstream_id")
    private String upstreamId;

    @JsonProperty("status")
    @Builder.Default
    private int status = 1;
}