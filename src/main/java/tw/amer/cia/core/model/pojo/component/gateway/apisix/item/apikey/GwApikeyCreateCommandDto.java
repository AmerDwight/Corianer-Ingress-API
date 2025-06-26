package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.apikey;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"username", "plugins"})
public class GwApikeyCreateCommandDto implements Serializable
{
    @JsonProperty("username")
    private String nameInRoleDashKeyName;
    private Plugins plugins;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonPropertyOrder({"keyAuth"})
    public static class Plugins
    {
        @JsonProperty("key-auth")
        private KeyAuth keyAuth;

        // KeyAuth inner class
        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        @JsonPropertyOrder({"key"})
        public static class KeyAuth
        {
            @JsonProperty("key")
            private String key;

        }
    }
}
