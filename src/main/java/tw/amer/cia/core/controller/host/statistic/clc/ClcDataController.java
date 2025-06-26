package tw.amer.cia.core.controller.host.statistic.clc;

import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.service.host.statistic.clc.ClcDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@HostRestController
@RequestMapping("/statistic/clc")
@ConditionalOnProperty(name = "coriander-ingress-api.host.clc.enable-clc", havingValue = "true")
public class ClcDataController {
    @Autowired
    ClcDataService clcDataService;

    @PatchMapping("/history/all")
    public void sendingAllHistoryDataFromCentralDatabase() throws DataSourceAccessException {
        clcDataService.sendingAllHistoryDataFromCentralDatabase();
    }
}
