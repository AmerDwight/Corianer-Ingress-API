package tw.amer.cia.core.component.functional.gateway.plugin.apisix;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.component.functional.gateway.plugin.GatewayPluginTemplate;
import tw.amer.cia.core.component.functional.gateway.plugin.GatewayPluginTemplateLoader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CiaRequestVerifyTemplate extends GatewayPluginTemplate {
    private final String APISIX_PLUGIN_NAME = "cia-request-verify";
    private final String PLUGIN_ATTR_SOURCE = "verify_source";
    private final String PLUGIN_ATTR_FIELD = "verify_field";
    private final String PLUGIN_ATTR_VALUE = "verify_value";

    @Override
    public String produceGatewayDeployCommand(String parameter) {
        try {
            Map<String, String> parameterMap = new HashMap<>();
            if (StringUtils.isNotBlank(parameter)) {
                parameterMap = GatewayPluginTemplateLoader.base64Decode(parameter);
            }
            CimRequestVerifyObject result = constructDataFromParameterMap(parameterMap);
            if (result != null) {
                return GatewayPluginTemplateLoader.ObjectMapperForPlugins.writeValueAsString(result);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        throw new RuntimeException(ErrorConstantLib.UNKNOWN_EXCEPTION_API_ERROR.getCompleteMessage());
    }

    @Override
    public String produceGatewayUndeployCommand(String gwData, String onDeleteParameters) {
        if (StringUtils.isNotBlank(gwData)) {
            try {
                Map<String, String> onRemoveParameterMap = new HashMap<>();
                if (StringUtils.isNotBlank(onDeleteParameters)) {
                    onRemoveParameterMap = GatewayPluginTemplateLoader.base64Decode(onDeleteParameters);
                }
                CimRequestVerifyObject result = constructDataFromParameterMap(false, onRemoveParameterMap);
                if (result != null) {
                    return GatewayPluginTemplateLoader.ObjectMapperForPlugins.writeValueAsString(result);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public String getEncodedParameters(Map<String, String> parameterMaps) {
        if (isValidParameter(parameterMaps)) {
            try {
                return GatewayPluginTemplateLoader.base64Encode(parameterMaps);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        throw new RuntimeException("Parameters for " + "CiaRequestVerifyTemplate " + "is illegal.");
    }

    private boolean isValidParameter(Map<String, String> parameterMaps) {
        if (MapUtils.isNotEmpty(parameterMaps)) {
            String source = getSourceTypeFromString(parameterMaps.get(PLUGIN_ATTR_SOURCE));
            String field = parameterMaps.get(PLUGIN_ATTR_FIELD);
            String value = parameterMaps.get(PLUGIN_ATTR_VALUE);
            if (StringUtils.isNotBlank(source) &&
                    StringUtils.isNotBlank(field) &&
                    StringUtils.isNotBlank(value)) {
                return true;
            }
        }
        throw new IllegalArgumentException(ErrorConstantLib.CRITICAL_PROPERTY_MISSING_OR_WRONG +
                PLUGIN_ATTR_SOURCE + " " +
                PLUGIN_ATTR_FIELD + " " +
                PLUGIN_ATTR_VALUE + ".");
    }

    @Nullable
    private CimRequestVerifyObject constructDataFromParameterMap(Map<String, String> attributes) {
        return constructDataFromParameterMap(true, attributes);
    }

    @Nullable
    private CimRequestVerifyObject constructDataFromParameterMap(Boolean enabled, Map<String, String> attributes) {
        String source = getSourceTypeFromString(attributes.get(PLUGIN_ATTR_SOURCE));
        String field = attributes.get(PLUGIN_ATTR_FIELD);
        String value = attributes.get(PLUGIN_ATTR_VALUE);
        if (StringUtils.isNotBlank(source) &&
                StringUtils.isNotBlank(field) &&
                StringUtils.isNotBlank(value)) {
            return new CimRequestVerifyObject(enabled, source, field, value);
        }
        return null;
    }

    @Nullable
    private String getSourceTypeFromString(String onJudgeContent) {
        if (StringUtils.isNotBlank(onJudgeContent)) {
            for (SourceType sourceType : SourceType.values()) {
                if (StringUtils.equals(sourceType.content, onJudgeContent.toLowerCase())) {
                    return sourceType.content;
                }
            }
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    private class CimRequestVerifyObject {
        @JsonProperty("plugins")
        private PluginTemplate plugin;

        private CimRequestVerifyObject(Boolean enabled, String verifySource, String verifyField, String verifyValue) {
            this.plugin = new PluginTemplate(
                    new PluginAttributeTemplate(
                            new PluginControlTemplate(!enabled),
                            verifySource,
                            verifyField,
                            verifyValue));
        }
    }

    @Data
    @AllArgsConstructor
    private class PluginTemplate {
        @JsonProperty("cia-request-verify")
        private PluginAttributeTemplate pluginAttributes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class PluginAttributeTemplate {
        @JsonProperty("_meta")
        private PluginControlTemplate pluginControlTemplate;

        @JsonProperty("verify_source")
        private String verifySource;

        @JsonProperty("verify_field")
        private String verifyField;

        @JsonProperty("verify_value")
        private String verifyValue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class PluginControlTemplate {
        @JsonProperty("disable")
        private boolean disable = false;
    }

    private enum SourceType {
        XML("xml"),
        JSON("json"),
        QUERY_PARAMETER("query_parameter"),
        HEADER("header");

        private String content;

        SourceType(String _content) {
            this.content = _content;
        }
    }

}
