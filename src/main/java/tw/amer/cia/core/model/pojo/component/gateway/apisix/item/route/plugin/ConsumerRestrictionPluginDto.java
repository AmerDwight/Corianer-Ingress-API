package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.plugin;

import tw.amer.cia.core.common.gateway.GatewayMessageLib;
import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.MetaDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ConsumerRestrictionPluginDto implements Serializable {
    @JsonProperty("rejected_code")
    @Builder.Default
    private Integer rejectedCode = HttpStatus.UNAUTHORIZED.value();

    @JsonProperty("rejected_msg")
    @Builder.Default
    private String rejectedMsg = GatewayMessageLib.gatewayMessageWrapper(GatewayMessageLib.GATEWAY_ROUTE_ACCESS_DENY_APIKEY_UNAUTHORIZED);

    @JsonProperty("whitelist")
    @NotNull
    private List<String> whitelist;

    @JsonProperty("_meta")
    @Builder.Default
    private MetaDto meta = MetaDto.builder().build();

    public static List<String> getDefaultWhiteList() {
        return new ArrayList<>(Collections.singletonList("ADMIN"));
    }
}
