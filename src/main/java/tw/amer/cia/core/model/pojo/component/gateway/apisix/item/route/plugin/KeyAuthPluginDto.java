package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.plugin;

import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.MetaDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Data
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class KeyAuthPluginDto implements Serializable
{

    @JsonProperty("_meta")
    @Builder.Default
    private MetaDto meta = MetaDto.builder().build();

    @JsonProperty("header")
    @Builder.Default
    private String header = "api-key";

    @JsonProperty("hide_credentials")
    @Builder.Default
    private boolean hideCredentials = false;

    @JsonProperty("query")
    @Builder.Default
    private String query = "api-key";
}