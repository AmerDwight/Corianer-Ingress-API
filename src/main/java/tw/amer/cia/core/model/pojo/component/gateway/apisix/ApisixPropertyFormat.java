package tw.amer.cia.core.model.pojo.component.gateway.apisix;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ApisixPropertyFormat implements Serializable {
    private String siteName;
    private List<String> fab;
    private String internalGatewayHost;
    private int internalGatewayAdminPort;
    private int internalGatewayServicePort;
    private String externalGatewayHost;
    private int externalGatewayServicePort;
    private String externalGrafanaHost;
    private int externalGrafanaPort;
    private String adminKey;

    private String logServerScheme;
    private String logServer;
    private int logServerPort;
    private String logServerPath;
    private String logServerAuthKey;

    private String gatewayProxyRedirectHost;
    private int gatewayProxyRedirectPort;
    private String gatewayProxyRedirectHeaderHost;
    private String gatewayProxyRedirectHeaderPort;
}
