package tw.amer.cia.core.service.client;

import tw.amer.cia.core.component.functional.gateway.GatewayControlHelper;
import tw.amer.cia.core.component.structural.annotation.ClientService;
import tw.amer.cia.core.model.pojo.service.common.gateway.ExternalGatewayInfoListDto;
import org.springframework.beans.factory.annotation.Autowired;

@ClientService
public class DataSynchronizeServiceForClient {
    @Autowired
    GatewayControlHelper gatewayControlHelper;

    public ExternalGatewayInfoListDto provideExternalGatewayInfoList(){
        return gatewayControlHelper.provideExternalGatewayInfoList();
    }
}
