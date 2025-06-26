package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.plugin;

import tw.amer.cia.core.common.gateway.GatewayMessageLib;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.MetaDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IpRestrictionPluginDto implements Serializable
{

    @JsonProperty("_meta")
    @Builder.Default
    private MetaDto meta = MetaDto.builder().build();

    @JsonProperty("whitelist")
    @Builder.Default
    private List<String> whitelist = new ArrayList<>();

    @JsonProperty("message")
    @Builder.Default
    private String message = GatewayMessageLib.gatewayMessageWrapper(GatewayMessageLib.GATEWAY_ROUTE_ACCESS_DENY_DEVICE_UNAUTHORIZED);

}