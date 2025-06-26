package tw.amer.cia.core.common.gateway;

public class GatewayMessageLib {
    public static final String GATEWAY_MESSAGE_HEADER = "Gateway Message: ";
    public static final String GATEWAY_ROUTE_ACCESS_DENY_APIKEY_UNAUTHORIZED = "Service access denied, unauthorized apikey.";
    public static final String GATEWAY_ROUTE_ACCESS_DENY_DEVICE_UNAUTHORIZED = "Service access denied, unauthorized device.";

    public static String gatewayMessageWrapper(String gatewayMessage) {
        return GATEWAY_MESSAGE_HEADER + gatewayMessage;
    }

}
