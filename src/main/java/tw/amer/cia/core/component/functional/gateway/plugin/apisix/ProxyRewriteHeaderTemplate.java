package tw.amer.cia.core.component.functional.gateway.plugin.apisix;

import tw.amer.cia.core.common.utility.JsonStringProcessor;
import tw.amer.cia.core.component.functional.gateway.plugin.GatewayPluginTemplate;
import tw.amer.cia.core.component.functional.gateway.plugin.GatewayPluginTemplateLoader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class ProxyRewriteHeaderTemplate extends GatewayPluginTemplate {
    private final String APISIX_PROXY_WRITE_HEADER = "proxy-rewrite";

    @Override
    public String produceGatewayDeployCommand(String parameter) {
        if (StringUtils.isNotEmpty(parameter)) {
            try {
                Map<String, String> parameterMap = GatewayPluginTemplateLoader.base64Decode(parameter);
                if (MapUtils.isNotEmpty(parameterMap)) {
                    ProxyRewriteHeaderObject result = new ProxyRewriteHeaderObject(parameterMap);
                    if (result != null) {
                        return GatewayPluginTemplateLoader.ObjectMapperForPlugins.writeValueAsString(result);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        throw new RuntimeException("Errors occurred while parsing  parameters.");
    }

    @Override
    public String produceGatewayUndeployCommand(String gwData, String onDeleteParameters) {
        if (StringUtils.isNotBlank(gwData) && StringUtils.isNotBlank(onDeleteParameters)) {
            try {
                Map<String, String> onRemoveParameterMap = GatewayPluginTemplateLoader.base64Decode(onDeleteParameters);
                if (MapUtils.isNotEmpty(onRemoveParameterMap)) {
                    return JsonStringProcessor.extractTagWithParent(
                            JsonStringProcessor.removeTagsFromJsonString(gwData, onRemoveParameterMap.keySet()),
                            APISIX_PROXY_WRITE_HEADER);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public String getEncodedParameters(Map<String, String> parameterMaps) {
        if (MapUtils.isNotEmpty(parameterMaps)) {
            try {
                return GatewayPluginTemplateLoader.base64Encode(parameterMaps);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        throw new RuntimeException("Parameters for " + "ProxyRewriteHeaderTemplate " + "is illegal.");
    }

    @Data
    @AllArgsConstructor
    private class ProxyRewriteHeaderObject {
        @JsonProperty("plugins")
        private TemplatePlugins plugins;

        private ProxyRewriteHeaderObject(Map<String, String> headers) {
            this.plugins = new TemplatePlugins(
                    new TemplateProxyRewrite(
                            headers
                    )
            );
        }
    }

    @Data
    @AllArgsConstructor
    private class TemplatePlugins {
        @JsonProperty("proxy-rewrite")
        private TemplateProxyRewrite proxyRewrite;
    }

    @Data
    @AllArgsConstructor
    private class TemplateProxyRewrite {
        @JsonProperty("headers")
        private Map<String, String> headers;
    }

}
