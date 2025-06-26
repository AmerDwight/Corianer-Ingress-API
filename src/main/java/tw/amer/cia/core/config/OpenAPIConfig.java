package tw.amer.cia.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tw.amer.cia.core.common.GeneralSetting;

@Configuration
public class OpenAPIConfig {
    // http://localhost:8080/swagger-ui/index.html
    @Value("${application-core.info.artifactId}")
    private String projectArtifactId;

    @Value("${application-core.info.version}")
    private String projectVersion;

    @Value("${application-core.info.description}")
    private String projectDesc;

    @Value("${coriander-ingress-api.setting.deploy-type}")
    private String deployType;

    @Value("${coriander-ingress-api.host.display-name}")
    private String deployHostName;

    @Value("${coriander-ingress-api.client.display-name}")
    private String deployClientName;


    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(projectArtifactId + " API Documents")
                        .description(projectDesc)
                        .version("v" + projectVersion)
                );
    }
    @Bean
    public GroupedOpenApi hostApi() {
        String deployName = StringUtils.equalsIgnoreCase(GeneralSetting.CiaDeployType.HOST.getDisplayName(), deployType) ? deployHostName : deployClientName;
        return GroupedOpenApi.builder()
                .group(deployName)
                .packagesToScan("tw.amer.cia")
                .pathsToMatch("/**")
                .build();
    }
}
