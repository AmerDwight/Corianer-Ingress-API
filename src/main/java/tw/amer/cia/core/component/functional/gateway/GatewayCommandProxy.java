package tw.amer.cia.core.component.functional.gateway;

import tw.amer.cia.core.exception.gateway.GatewayControllerException;

public interface GatewayCommandProxy {
    void patchGwRouteCommand(String deployFabId, Object commandDto, String gwRouteId) throws GatewayControllerException;

}
