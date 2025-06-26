package tw.amer.cia.core.config.core;

import tw.amer.cia.core.component.structural.resource.sqlCommandLoader.H2SqlCommander;
import tw.amer.cia.core.component.structural.resource.sqlCommandLoader.MariadbSqlCommander;
import tw.amer.cia.core.component.structural.resource.sqlCommandLoader.OracleSqlCommander;
import tw.amer.cia.core.component.structural.resource.sqlCommandLoader.SqlCommander;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class SqlCommanderConfig
{

    @Bean
    @ConditionalOnProperty(name = "coriander-ingress-api.setting.datasource-type", havingValue = "mariadb")
    public SqlCommander mariadbSqlCommander(ResourceLoader resourceLoader)
    {
        return new MariadbSqlCommander(resourceLoader);
    }

    @Bean
    @ConditionalOnProperty(name = "coriander-ingress-api.setting.datasource-type", havingValue = "oracle")
    public SqlCommander oracleSqlCommander(ResourceLoader resourceLoader)
    {
        return new OracleSqlCommander(resourceLoader);
    }

    @Bean
    @ConditionalOnProperty(name = "coriander-ingress-api.setting.datasource-type", havingValue = "h2")
    public SqlCommander h1SqlCommander(ResourceLoader resourceLoader)
    {
        return new H2SqlCommander(resourceLoader);
    }
}
