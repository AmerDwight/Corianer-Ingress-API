package tw.amer.cia.core.controller.client.synchronize;

import tw.amer.cia.core.component.structural.annotation.ClientRestController;
import tw.amer.cia.core.service.client.DataSynchronizeServiceForClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@ClientRestController
@RequestMapping("/synchronize/data")
public class SynchronizeControllerForClient {

    @Autowired
    DataSynchronizeServiceForClient dataSynchronizeService;

    @GetMapping("/gateway")
    public Object provideGatewayInformation(){
        return dataSynchronizeService.provideExternalGatewayInfoList();
    }

}
