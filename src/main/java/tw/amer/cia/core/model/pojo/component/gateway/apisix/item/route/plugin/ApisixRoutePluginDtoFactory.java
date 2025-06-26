package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.plugin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * For those require controlled by conditions, we mark constructor as packaged access, and use factory instead.
 */
@Slf4j
@Component
public class ApisixRoutePluginDtoFactory {

    @Value("${api-gateway.gw-request-verify:true}")
    private boolean enableGatewayRequestVerify;

    public ConsumerRestrictionPluginDto createConsumerRestrictionDto(List<String> whitelist) {
        ConsumerRestrictionPluginDto dto = ConsumerRestrictionPluginDto.builder()
                .whitelist(whitelist)
                .build();
        if (!enableGatewayRequestVerify) {
            dto.getMeta().setDisable(true);
        }
        return dto;
    }

    public KeyAuthPluginDto createKeyAuthPluginDto() {
        KeyAuthPluginDto dto = KeyAuthPluginDto.builder()
                .build();
        if (!enableGatewayRequestVerify) {
            dto.getMeta().setDisable(true);
        }
        return dto;
    }
}
