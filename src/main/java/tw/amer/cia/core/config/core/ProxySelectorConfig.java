package tw.amer.cia.core.config.core;

import tw.amer.cia.core.component.structural.httpClient.proxySelector.DynamicProxySelector;
import tw.amer.cia.core.component.structural.httpClient.proxySelector.FabProxySelector;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Data
@Configuration
public class ProxySelectorConfig
{
    @Bean
    public DynamicProxySelector buildFabProxySelector()
    {
        return new FabProxySelector();
    }

}

