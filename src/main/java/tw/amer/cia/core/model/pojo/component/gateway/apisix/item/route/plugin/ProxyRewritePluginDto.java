package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.plugin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProxyRewritePluginDto implements Serializable
{
    @JsonProperty("uri")
    private String uri;

    @JsonProperty("use_real_request_uri_unsafe")
    @Builder.Default
    private boolean useRealRequestUriUnsafe = false;

    @JsonProperty("headers")
    private Map<String, String> headers;
}