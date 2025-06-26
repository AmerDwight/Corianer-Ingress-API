package tw.amer.cia.core.component.structural.init;

import tw.amer.cia.core.component.structural.annotation.HostComponent;
import tw.amer.cia.core.component.structural.resource.sqlCommandLoader.SqlCommander;
import tw.amer.cia.core.service.core.GeneralService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@HostComponent
public class HostInitializer implements ApplicationRunner
{
    private final ConfigurableApplicationContext context;
    @Autowired
    GeneralService generalService;
    @Autowired
    SqlCommander sqlCommander;

    public HostInitializer(ConfigurableApplicationContext context)
    {
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception
    {

        // 定義初始化步驟：
        //  1. module Database Setting

        log.info("------------------------------------------------------------------------");
        log.info("Start Initializing : {}", StringUtils.defaultString(generalService.getDeployHostName()));
        log.info("Version : {}", generalService.getVersionString());

        // 1.0 module Database Setting
        ifNotSuccessThenShutdownHost(sqlCommander.initial());

        log.info("Initial Success.");
        log.info("------------------------------------------------------------------------");
    }

    private void ifNotSuccessThenShutdownHost(boolean necessarySuccess)
    {
        if (!necessarySuccess)
        {
            log.error("Calling client showdown... ");
            context.close();
            System.exit(1);
        }
    }
}
