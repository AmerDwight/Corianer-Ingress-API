package tw.amer.cia.core.component.structural.info;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.regex.Pattern;

@Component
@Slf4j
public class SystemConfiguration {

    private static final Pattern YAML_CONFIG_PATTERN = Pattern.compile(".*application\\S*\\.yml.*");
    private static final StringBuilder configDetails = new StringBuilder();
    private final Environment environment;

    public SystemConfiguration(Environment environment) {
        this.environment = environment;
    }

    public static String getCustomizedConfigs() {
        return configDetails.toString();
    }

    @PostConstruct
    public void logConfigurationDetails() {
        // Obtain all configs and record only customized config.
        MutablePropertySources sources = ((org.springframework.core.env.ConfigurableEnvironment) environment).getPropertySources();
        sources.forEach(source ->
        {
            if (YAML_CONFIG_PATTERN.matcher(source.getName()).matches()) {
                configDetails.append("Properties loaded from source: ").append(source.getName()).append("\n");
                collectProperties(source);
            }
        });
        log.info(configDetails.toString());
    }

    public String getConfigurationDetails() {
        MutablePropertySources sources = ((org.springframework.core.env.ConfigurableEnvironment) environment).getPropertySources();
        sources.forEach(source ->
        {
            if (YAML_CONFIG_PATTERN.matcher(source.getName()).matches()) {
                configDetails.append("Properties loaded from source: ").append(source.getName()).append("\n");
                collectProperties(source);
            }
        });
        return configDetails.toString();
    }

    private void collectProperties(PropertySource<?> source) {
        if (source.getSource() instanceof java.util.Map) {
            ((java.util.Map<?, ?>) source.getSource()).forEach((key, value) ->
            {
                String resolvedValue = environment.getProperty(key.toString());
                configDetails.append(key).append(" = ").append(resolvedValue).append("\n");
            });
        }
    }
}