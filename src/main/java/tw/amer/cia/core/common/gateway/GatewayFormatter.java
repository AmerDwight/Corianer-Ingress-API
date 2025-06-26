package tw.amer.cia.core.common.gateway;

public class GatewayFormatter
{
    private static final String GW_APIKEY_NAME_FORMAT = "{roleId}_{apiKeyName}";
    private static final String GW_UPSTREAM_NAME_FORMAT = "{fabId}_{systemName}";
    private static final String GW_ROUTE_NAME_FORMAT = "{fabId}_{systemName}_{apiName}_{postfix}";
    private static final String GW_SIMPLE_ROUTE_PATH_FORMAT = "/{fabId}/{systemName}/{endpointUri}";
    private static final String GW_COMPLETE_ROUTE_PATH_FORMAT = "{scheme}://{host}:{port}/{fabId}/{systemName}/{endpointUri}";

    public static String formatGwApikeyName(String roleId, String apiKeyName)
    {
        String newKeyName = GW_APIKEY_NAME_FORMAT
                .replace("{roleId}", roleId)
                .replace("{apiKeyName}", apiKeyName);
        return newKeyName;
    }

    public static String formatGwUpstreamName(String fabId, String systemName)
    {
        String newUpstreamName = GW_UPSTREAM_NAME_FORMAT
                .replace("{fabId}", fabId)
                .replace("{systemName}", systemName);
        return newUpstreamName;
    }

    public static String formatGwRouteName(String fabId, String systemName, String apiName, String postfix)
    {
        String newRouteName = GW_ROUTE_NAME_FORMAT
                .replace("{fabId}", fabId)
                .replace("{systemName}", systemName)
                .replace("{apiName}", apiName)
                .replace("{postfix}", postfix);
        return newRouteName;
    }

    public static String formatGwSimpleRoutePath(String fabId, String systemName, String endpoint)
    {
        String newPath = GW_SIMPLE_ROUTE_PATH_FORMAT
                .replace("{fabId}", fabId)
                .replace("{systemName}", systemName)
                .replace("{endpointUri}", endpoint);
        return newPath;
    }
    public static String formatGwCompleteRoutePath(boolean isHttps, String host, int port, String fabId, String systemName, String endpoint){
        if(isHttps){
            return formatGwCompleteRoutePath("https",host,port,fabId,systemName,endpoint);
        }else{
            return formatGwCompleteRoutePath("http",host,port,fabId,systemName,endpoint);
        }
    }
    public static String formatGwCompleteRoutePath(String scheme, String host, int port, String fabId, String systemName, String endpoint)
    {
        String newPath = GW_COMPLETE_ROUTE_PATH_FORMAT
                .replace("{scheme}", scheme)
                .replace("{host}", host)
                .replace("{port}", String.valueOf(port))
                .replace("{fabId}", fabId)
                .replace("{systemName}", systemName)
                .replace("{endpointUri}", removeLeadingSlash(endpoint));
        return newPath;
    }

    private static String removeLeadingSlash(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        int startIndex = 0;
        while (startIndex < input.length() && (input.charAt(startIndex) == '/' || input.charAt(startIndex) == '\\')) {
            startIndex++;
        }

        return input.substring(startIndex);
    }
}
