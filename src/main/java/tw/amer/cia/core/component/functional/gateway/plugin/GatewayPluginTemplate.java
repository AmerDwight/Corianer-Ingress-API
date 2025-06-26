package tw.amer.cia.core.component.functional.gateway.plugin;

import java.util.Map;

public abstract class GatewayPluginTemplate {
    public abstract String produceGatewayDeployCommand(String parameters);

    public abstract String produceGatewayUndeployCommand(String gwData ,String onDeleteParameters);

    public abstract String getEncodedParameters(Map<String,String> parameterMaps);

}
