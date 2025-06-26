package tw.amer.cia.core.component.functional.gateway;

import tw.amer.cia.core.component.structural.httpClient.HttpRequestSender;
import tw.amer.cia.core.component.structural.property.ApisixProperties;
import tw.amer.cia.core.exception.gateway.ApisixProcessorException;
import tw.amer.cia.core.exception.gateway.GatewayControllerException;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.ApisixPropertyFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
public class ApisixCommandProxy extends HttpRequestSender implements GatewayCommandProxy {

    @Autowired
    ApisixProperties configProperties;

    // Command Path
    private final String MANAGE_ROUTE_URI_WITH_ID = "/apisix/admin/routes/{id}";

    private boolean IS_USE_HTTPS;

    public ApisixCommandProxy() {
        super();
    }

    @PostConstruct
    public void initApisixControlHelper() {
        this.IS_USE_HTTPS = StringUtils.equalsIgnoreCase("HTTPS", configProperties.getScheme());
    }

    @Override
    public void patchGwRouteCommand(String deployFabId, Object commandDto, String gwRouteId) throws GatewayControllerException {
        ApisixPropertyFormat connectionInfo = configProperties.getPropertiesByFab(deployFabId);

        String updateInfoUrl = ApisixControlHelper.buildUrl(IS_USE_HTTPS, connectionInfo.getInternalGatewayHost(), connectionInfo.getInternalGatewayAdminPort(),
                UriComponentsBuilder.fromPath(MANAGE_ROUTE_URI_WITH_ID).buildAndExpand(gwRouteId).toString());
        HttpEntity<Object> updateEntity = new HttpEntity<>(commandDto, gatewayControlHeader(connectionInfo.getAdminKey(), null));

        // Send retrieve request
        sendGatewayHttpCommand(updateEntity, HttpMethod.PATCH, updateInfoUrl);
    }


    private HttpHeaders gatewayControlHeader(String adminKey, Map<String, String> optionalHeaders) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", adminKey);
        headers.set("Content-Type", "application/json");

        // Check if optionalHeaders is provided and not empty
        if (MapUtils.isNotEmpty(optionalHeaders)) {
            for (Map.Entry<String, String> header : optionalHeaders.entrySet()) {
                headers.set(header.getKey(), header.getValue());
            }
        }
        return headers;
    }

    private ResponseEntity<String> sendGatewayHttpCommand(HttpEntity<Object> httpEntity, HttpMethod httpMethod, String url) throws ApisixProcessorException {
        try {
            return this.sendHttpCommand(httpEntity, httpMethod, url, String.class);
        } catch (HttpClientErrorException e) {
            log.error("Error Response Status Code: {}", e.getStatusCode());
            log.error("Error Response Body: {}", "APISIX：" + e.getResponseBodyAsString());
            throw ApisixProcessorException.createExceptionForHttp(e.getStatusCode(), e.getResponseBodyAsString()); // Re-throw the exception to let the caller handle it, or you can handle it based on your application's requirements
        } catch (Exception e) {
            log.error("An unexpected error occurred during {} processing, for sending the HTTP request to URL: {}", Thread.currentThread().getStackTrace()[2].getMethodName(), url, e);
            throw e; // Generic catch to handle other types of exceptions
        }
    }
}
