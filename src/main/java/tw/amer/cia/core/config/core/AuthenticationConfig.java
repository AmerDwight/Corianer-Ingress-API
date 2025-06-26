package tw.amer.cia.core.config.core;


import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import tw.amer.cia.core.service.host.auth.AuthenticationService;
import tw.amer.cia.core.service.host.auth.SampleAuthenticationService;

@Data
@Configuration
public class AuthenticationConfig {
    @Bean
    @ConditionalOnProperty(name = "coriander-ingress-api.setting.deploy-type", havingValue = "host")
    public AuthenticationService authenticationService(ResourceLoader resourceLoader) {
        return new SampleAuthenticationService();
    }
}
