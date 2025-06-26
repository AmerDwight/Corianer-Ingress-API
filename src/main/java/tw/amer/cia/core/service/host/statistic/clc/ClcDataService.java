package tw.amer.cia.core.service.host.statistic.clc;


import tw.amer.cia.core.component.structural.annotation.HostService;
import tw.amer.cia.core.component.functional.statistic.clc.ClcMessageSender;
import tw.amer.cia.core.component.functional.statistic.clc.supplier.ClcHistoryLogSupplierLib;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.pojo.service.host.statistic.clc.ClcLogMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Slf4j
@HostService
@ConditionalOnProperty(name = "coriander-ingress-api.host.clc.enable-clc", havingValue = "true")
public class ClcDataService {

    @Autowired
    ClcMessageSender clcMessageSender;

    @Autowired
    ClcHistoryLogSupplierLib clcHistoryLogSupplierLib;

    public void sendingAllHistoryDataFromCentralDatabase() throws DataSourceAccessException {
        for (ClcLogMessage clcMessage : clcHistoryLogSupplierLib.getAllHistoryCiaLogMessage()) {
            log.info("On Send clc Message: {}", clcMessage.toString());
            clcMessageSender.addMessage(clcMessage);
        }
    }
}
