package tw.amer.cia.core.component.structural.httpClient;

import tw.amer.cia.core.component.structural.httpClient.proxySelector.DynamicProxySelector;
import tw.amer.cia.core.component.structural.property.CoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class HttpRequestSender {
    protected final boolean enableProxy;
    protected RestTemplate restTemplate;

    @Autowired
    DynamicProxySelector dynamicProxySelector;
    @Autowired
    protected CoreProperties coreProperties;

    @Value("${request.settings.timeout-ms}")
    private final Integer REQUEST_TIMEOUT = 12000;

    public HttpRequestSender() {
        this(false);
    }

    public HttpRequestSender(boolean _enableProxy) {
        this.enableProxy = _enableProxy;
    }

    public static String buildUrl(boolean useHttps, String host, int port, String uri) {
        boolean needUriFix = !uri.startsWith("/");
        if (needUriFix) {
            uri = "/" + uri;
        }
        if (useHttps) {
            return String.format("https://%s:%d%s", host, port, uri);
        } else {
            return String.format("http://%s:%d%s", host, port, uri);
        }
    }

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate(getDefaultHttpRequestFactory(this.enableProxy));
    }

    protected HttpHeaders apiControlHeader(String adminKey, Map<String, String> optionalHeaders) {
        String controlHeader = coreProperties.getSetting().getApi().getControlHeader();
        HttpHeaders headers = new HttpHeaders();
        headers.set(controlHeader, adminKey);
        headers.set("Content-Type", "application/json");
        headers.set(coreProperties.getIdentifyHeader(), coreProperties.getSelfIdentify());

        // Check if optionalHeaders is provided and not empty
        if (MapUtils.isNotEmpty(optionalHeaders)) {
            for (Map.Entry<String, String> header : optionalHeaders.entrySet()) {
                headers.set(header.getKey(), header.getValue());
            }
        }
        return headers;
    }

    protected HttpComponentsClientHttpRequestFactory getDefaultHttpRequestFactory(boolean enableProxy) {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(this.REQUEST_TIMEOUT)
                .setSocketTimeout(this.REQUEST_TIMEOUT)
                .build();
        if (enableProxy) {
            return new HttpComponentsClientHttpRequestFactory(
                    HttpClientBuilder.create()
                            .setRoutePlanner(new SystemDefaultRoutePlanner(dynamicProxySelector))
                            .setDefaultRequestConfig(config)
                            .build());
        } else {
            return new HttpComponentsClientHttpRequestFactory(
                    HttpClientBuilder.create()
                            .setDefaultRequestConfig(config)
                            .build());
        }
    }

    protected ResponseEntity<String> sendHttpCommand(HttpEntity<?> httpEntity, HttpMethod httpMethod, String url) {
        return this.sendHttpCommand(httpEntity, httpMethod, url, String.class);
    }

    protected ResponseEntity<String> sendHttpCommandWithFaultTolerance(HttpEntity<?> httpEntity, HttpMethod httpMethod, String url) {
        return this.sendHttpCommandWithFaultTolerance(httpEntity, httpMethod, url, String.class);
    }



    public <T> ResponseEntity<T> sendHttpCommand(HttpEntity<?> httpEntity, HttpMethod httpMethod, String url, Class<T> responseType) {
        log.info("Sending Request at: {} ", this.getClass().getSimpleName());
        log.info("Request URL: {}, Method: {}, Body: {} ", url, httpMethod, httpEntity.getBody());
        return this.restTemplate.exchange(url, httpMethod, httpEntity, responseType);
    }

    public <T> ResponseEntity<T> sendHttpCommandWithFaultTolerance(HttpEntity<?> httpEntity, HttpMethod httpMethod, String url, Class<T> responseType) {
        log.info("Sending Request at: {} ", this.getClass().getSimpleName());
        log.info("Request URL: {}, Method: {}, Body: {} ", url, httpMethod, httpEntity.getBody());
        try {
//            return this.restTemplate.exchange(url, httpMethod, httpEntity, responseType);
            ResponseEntity<T> response = this.restTemplate.exchange(url, httpMethod, httpEntity, responseType);
            log.info("Received response for URL: {} with status: {}", url, response.getStatusCode());
            return response;
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Received client/server error for URL: {}, Status: {}, Error Body: {}", url, ex.getStatusCode(), ex.getResponseBodyAsString());
            return ResponseEntity.status(ex.getStatusCode())
                    .headers(ex.getResponseHeaders())
                    .body((T) ex.getResponseBodyAsString());
        } catch (Exception ex) {
            log.error("Unexpected error occurred while sending request to URL: {}, Error: {}", url, ex.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    protected Map<String, Boolean> broadcastSender(Map<String, HttpEntity<Object>> httpEntityByUrlMap, HttpMethod httpMethod) {
        ExecutorService executorService = Executors.newFixedThreadPool(Math.min(httpEntityByUrlMap.size(), 10));
        List<Future<Map.Entry<String, Boolean>>> futures = new ArrayList<>();
        Map<String, Boolean> results = new LinkedHashMap<>();

        try {
            for (Map.Entry<String, HttpEntity<Object>> entry : httpEntityByUrlMap.entrySet()) {
                String url = entry.getKey();
                Callable<Map.Entry<String, Boolean>> task = () ->
                {
                    try {
                        log.info("Sending HTTP request to {}", url);
                        ResponseEntity<String> commandResult = sendHttpCommand(entry.getValue(), httpMethod, url);
                        log.info("Received HTTP response from {}: Status Code: {}, Body: {}", url, commandResult.getStatusCode(), commandResult.getBody());
                        return new AbstractMap.SimpleEntry<>(url, commandResult.getStatusCode().is2xxSuccessful());
                    } catch (Exception e) {
                        log.error("Error sending HTTP request to {}: {}", url, e.getMessage(), e);
                        return new AbstractMap.SimpleEntry<>(url, false);
                    }
                };
                futures.add(executorService.submit(task));
                log.info("Task submitted for URL: {}", url);
            }

            // 蒐集所有請求的結果
            for (Future<Map.Entry<String, Boolean>> future : futures) {
                Map.Entry<String, Boolean> result = future.get();
                results.put(result.getKey(), result.getValue());
                log.info("Task completed for URL: {} with result: {}", result.getKey(), result.getValue());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Exception in processing tasks: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            httpEntityByUrlMap.keySet().forEach(url -> results.put(url, false));
        } finally {
            executorService.shutdown();
            log.info("Broadcast Sender finished work.");
        }

        return results;
    }

    protected <T> Map<String, T> broadcastSender(Map<String, HttpEntity<Object>> httpEntityByUrlMap,
                                                 HttpMethod httpMethod,
                                                 Class<T> responseType) {

        ExecutorService executorService = Executors.newFixedThreadPool(Math.min(httpEntityByUrlMap.size(), 10));
        List<Future<Map.Entry<String, T>>> futures = new ArrayList<>();
        Map<String, T> results = new LinkedHashMap<>();

        try {
            for (Map.Entry<String, HttpEntity<Object>> entry : httpEntityByUrlMap.entrySet()) {
                String url = entry.getKey();
                Callable<Map.Entry<String, T>> task = () -> {
                    try {
                        log.info("Sending HTTP request to {} with expected response type: {}", url, responseType.getSimpleName());
                        ResponseEntity<T> response = restTemplate.exchange(url, httpMethod, entry.getValue(), responseType);

                        log.info("Received HTTP response from {}: Status Code: {}", url, response.getStatusCode());
                        if (response.getBody() != null) {
                            log.debug("Response body from {}: {}", url, response.getBody());
                            return new AbstractMap.SimpleEntry<>(url, response.getBody());
                        } else {
                            log.warn("Received null response body from {}", url);
                            return new AbstractMap.SimpleEntry<>(url, null);
                        }
                    } catch (Exception e) {
                        log.error("Error sending HTTP request to {}: {}", url, e.getMessage(), e);
                        return new AbstractMap.SimpleEntry<>(url, null);
                    }
                };
                futures.add(executorService.submit(task));
                log.info("Task submitted for URL: {}", url);
            }

            // 蒐集所有請求的回應
            for (Future<Map.Entry<String, T>> future : futures) {
                // Map.Entry<String, T> 的 Key 為 url，詳見task
                Map.Entry<String, T> result = future.get();
                results.put(result.getKey(), result.getValue());
                log.info("Task completed for URL: {} with response received: {}",
                        result.getKey(),
                        result.getValue() != null ? "success" : "null");
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Exception in processing tasks: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            httpEntityByUrlMap.keySet().forEach(url -> results.put(url, null));
        } finally {
            executorService.shutdown();
            log.info("Broadcast Sender finished work. Total results collected: {}", results.size());
        }

        return results;
    }

    protected void configureHttpsClient() {
        try {
            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, (certificate, authType) -> true)
                    .build();

            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                    sslContext, NoopHostnameVerifier.INSTANCE);

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(socketFactory)
                    .build();

            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

            this.restTemplate = new RestTemplate(factory);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }
}
