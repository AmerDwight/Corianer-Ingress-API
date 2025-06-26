package tw.amer.cia.core.service.host.cron;

import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.service.host.GatewayInfoServiceForHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class GatewayInfoCronJob {
    @Autowired
    GatewayInfoServiceForHost gatewayInfoServiceForHost;

    @Scheduled(initialDelay = 5 * 60 * 1000, fixedRate = 180 * 60 * 1000)
    public void updateExternalGatewayInfoFromClient() throws CiaProcessorException {
        gatewayInfoServiceForHost.updateExternalGatewayInfoFromClient();
    }
}
