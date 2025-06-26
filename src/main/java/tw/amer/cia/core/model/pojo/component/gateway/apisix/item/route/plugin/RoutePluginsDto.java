package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.plugin;

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
public class RoutePluginsDto implements Serializable
{
    @JsonProperty("consumer-restriction")
    private ConsumerRestrictionPluginDto consumerRestriction;

    @JsonProperty("key-auth")
    private KeyAuthPluginDto keyAuth;

    @JsonProperty("ip-restriction")
    private IpRestrictionPluginDto ipRestriction;

    @JsonProperty("proxy-rewrite")
    private ProxyRewritePluginDto proxyRewritePluginDto;

    @JsonProperty("cim-request-verify")
    private CiaRequestVerifyPluginDto ciaRequestVerifyPluginDto;
}
