package tw.amer.cia.core.controller.client.webApi;

import tw.amer.cia.core.component.functional.gateway.GatewayControlHelper;
import tw.amer.cia.core.component.structural.annotation.ClientRestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@ClientRestController
@RequestMapping("/${coriander-ingress-api.client.display-name}")
public class GatewayInfoController
{
    @Autowired
    GatewayControlHelper gatewayControlHelper;

    @GetMapping("/gateway/general/healthcheck")
    public Object listGwRouteInfo()
    {
        boolean checkingAllGatewayAlive = gatewayControlHelper.checkAllGatewayAlive();
        if (checkingAllGatewayAlive)
        {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.SERVICE_UNAVAILABLE);
    }

}
