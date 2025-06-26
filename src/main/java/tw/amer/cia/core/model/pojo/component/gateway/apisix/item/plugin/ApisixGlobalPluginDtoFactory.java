package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.plugin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ApisixGlobalPluginDtoFactory {
    @Value("${api-gateway.http-log-collect:true}")
    private boolean enableHttpLogCollect;

    public HttpLoggerPluginDto createHttpLoggerPluginDto(@NonNull String uri,@NonNull String authKey) {
        HttpLoggerPluginDto dto = HttpLoggerPluginDto.builder()
                .uri(uri)
                .authKey(authKey)
                .build();
        if (!enableHttpLogCollect) {
            dto.getMeta().setDisable(true);
        }
        return dto;
    }
}
