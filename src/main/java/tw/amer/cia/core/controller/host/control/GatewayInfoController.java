package tw.amer.cia.core.controller.host.control;

import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.component.structural.annotation.RequireExtEntityVerifyApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@HostRestController
@RequireExtEntityVerifyApi
@RequestMapping("/${coriander-ingress-api.host.display-name}")
public class GatewayInfoController
{

}
