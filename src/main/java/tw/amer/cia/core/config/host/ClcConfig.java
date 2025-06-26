package tw.amer.cia.core.config.host;

import tw.amer.cia.core.component.structural.annotation.HostConfiguration;
import tw.amer.cia.core.component.functional.statistic.clc.ClcMessageSender;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@Slf4j
@HostConfiguration
public class ClcConfig {

    @Value("${coriander-ingress-api.host.clc.enable-proxy:false}")
    @Setter
    private boolean isClcUseProxy;

    @Bean
    @ConditionalOnProperty(name = "coriander-ingress-api.host.clc.enable-clc", havingValue = "true")
    public ClcMessageSender createCiaLogMessageSender(){
        log.info("Host Configuration: Create Component: {}", ClcMessageSender.class.getSimpleName());
        return new ClcMessageSender(isClcUseProxy);
    }
}
