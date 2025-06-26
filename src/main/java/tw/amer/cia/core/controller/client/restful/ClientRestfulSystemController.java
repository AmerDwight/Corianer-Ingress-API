package tw.amer.cia.core.controller.client.restful;

import tw.amer.cia.core.component.structural.annotation.ClientRestController;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.exception.gateway.GatewayControllerException;
import tw.amer.cia.core.model.database.SystemEntity;
import tw.amer.cia.core.model.database.SystemDpyEntity;
import tw.amer.cia.core.service.client.SystemServiceForClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@ClientRestController
@RequestMapping("/${coriander-ingress-api.client.display-name}")
public class ClientRestfulSystemController {
    @Autowired
    SystemServiceForClient systemServiceForClient;

    @PostMapping("/restful/admin/system")
    public void createOrUpdateSystem(@Valid @RequestBody SystemEntity inDto) {
        // CGC提供對單一系統的更新、Gateway操作
        systemServiceForClient.createOrUpdateSystem(inDto);
    }

    @PostMapping("/restful/admin/system/deployment")
    public void createOrUpdateSystemDeployment(@Valid @RequestBody SystemDpyEntity inDto) throws GatewayControllerException, DataSourceAccessException {
        // CGC提供對單一系統的更新、Gateway操作
        systemServiceForClient.createOrUpdateSystemDeployment(inDto);
    }

    @DeleteMapping("/restful/admin/system/deployment")
    public void deleteSystemDeployment(@Valid @RequestBody SystemDpyEntity inDto) throws GatewayControllerException {
        // CGC提供對單一系統的部署解除、Gateway下架操作
        systemServiceForClient.deleteSystemDeployment(inDto);
    }
}
