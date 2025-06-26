package tw.amer.cia.core.config.gateway;

import tw.amer.cia.core.component.functional.gateway.ApisixCommandProxy;
import tw.amer.cia.core.component.functional.gateway.ApisixControlHelper;
import tw.amer.cia.core.component.functional.gateway.GatewayCommandProxy;
import tw.amer.cia.core.component.functional.gateway.GatewayControlHelper;
import tw.amer.cia.core.component.functional.gateway.plugin.GatewayPluginTemplateLoader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class GatewayConfig {
    @Bean
    @ConditionalOnExpression("'${api-gateway.gateway-type}'=='apisix' && '${coriander-ingress-api.setting.deploy-type}'=='client'")
    public GatewayControlHelper apisixGatewayCommander() {
        return new ApisixControlHelper();
    }

    @Bean
    @ConditionalOnProperty(name = "api-gateway.gateway-type", havingValue = "apisix")
    public GatewayPluginTemplateLoader apisixGatewayPluginTemplateLoader() {
        String gwPluginUrl = "tw.amer.cia.core.component.functional.gateway.plugin.apisix";
        return new GatewayPluginTemplateLoader(gwPluginUrl);
    }

    @Bean
    @ConditionalOnExpression("'${api-gateway.gateway-type}'=='apisix' && '${coriander-ingress-api.setting.deploy-type}'=='client'")
    public GatewayCommandProxy apisixGatewayCommandProxy() {
        return new ApisixCommandProxy();
    }
}
