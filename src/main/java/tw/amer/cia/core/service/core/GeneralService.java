package tw.amer.cia.core.service.core;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.component.structural.info.SystemConfiguration;
import tw.amer.cia.core.model.pojo.component.property.VersionSignature;

@Data
@Service
public class GeneralService {
    @Value("${application-core.info.groupId}")
    private String projectGroupId;

    @Value("${application-core.info.artifactId}")
    private String projectArtifactId;

    @Value("${application-core.info.version}")
    private String projectVersion;

    @Value("${application-core.info.name}")
    private String projectName;

    @Value("${application-core.info.description}")
    private String projectDesc;

    @Value("${coriander-ingress-api.setting.deploy-type}")
    private String deployType;

    @Value("${coriander-ingress-api.host.display-name}")
    private String deployHostName;

    @Value("${coriander-ingress-api.client.display-name}")
    private String deployClientName;

    @Value("${coriander-ingress-api.setting.identify}")
    private String deployIdentifier;

    @Autowired
    SystemConfiguration systemConfiguration;

    public Object getVersion() {
        String deployName = StringUtils.equalsIgnoreCase(GeneralSetting.CiaDeployType.HOST.getDisplayName(), deployType) ? deployHostName : deployClientName;
        return new VersionSignature(projectName, projectVersion, deployType, deployName);
    }

    public Object getVersionString() {
        return this.getVersion().toString();
    }

    public Object getConfig() {
        return systemConfiguration.getConfigurationDetails();
    }
}
