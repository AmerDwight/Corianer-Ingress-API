package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.plugin;

import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.MetaDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;


@Data
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpLoggerPluginDto implements Serializable {

    @JsonProperty("_meta")
    @Builder.Default
    MetaDto meta = MetaDto.builder().build();

    @JsonProperty("auth_header")
    private String authKey = "http-log-server-pass-key";

    @JsonProperty("uri")
    @Builder.Default
    private String uri = "http-Log-server:12375/log/apisix";

}
