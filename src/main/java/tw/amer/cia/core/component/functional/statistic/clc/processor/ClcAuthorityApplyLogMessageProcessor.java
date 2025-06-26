package tw.amer.cia.core.component.functional.statistic.clc.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import tw.amer.cia.core.component.structural.annotation.HostComponent;
import tw.amer.cia.core.model.pojo.service.host.statistic.clc.CiaAuthorityApplyHisDto;
import tw.amer.cia.core.model.pojo.service.host.statistic.clc.ClcLogMessage;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@HostComponent
public class ClcAuthorityApplyLogMessageProcessor implements CiaLogMessageProcessor<CiaAuthorityApplyHisDto> {

    @Value("${coriander-ingress-api.host.clc.enable-clc}")
    private boolean enableClc;

    private final String CIA_LOG_MESSAGE_LABEL_TYPE = "role_authority_apply";

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    ;

    @Override
    public ClcLogMessage processLog(CiaAuthorityApplyHisDto logData) {
        if (!enableClc) {
            return null;
        }
        if (logData == null) {
            throw new IllegalArgumentException("Log data cannot be null");
        }

        try {
            // 創建標籤
            Map<String, String> labels = new HashMap<>();
            labels.put("app", "cia");
            labels.put("type", CIA_LOG_MESSAGE_LABEL_TYPE);
            labels.put("apply_form_id", nullSafeGet(logData.getApplyFormId()));
            labels.put("role_id", nullSafeGet(logData.getRoleId()));
            labels.put("ms_id", nullSafeGet(logData.getApiId()));
            labels.put("fab_id", nullSafeGet(logData.getFabId()));

            // 將整個對象轉換為JSON字符串
            String message = objectMapper.writeValueAsString(logData);

            // 創建 ClcLogMessage
            ClcLogMessage clcLogMessage = ClcLogMessage.builder()
                    .labels(labels)
                    .message(message)
                    .build();

            // 設置時間戳，優先使用 createTime，如果為空則使用當前時間
            if (logData.getCreateTime() != null) {
                clcLogMessage.setTimestamp(logData.getCreateTime());
            } else {
                clcLogMessage.setTimestamp(Instant.now());
            }

            return clcLogMessage;

        } catch (JsonProcessingException e) {
            log.error("Failed to process log data", e);
            throw new RuntimeException("Failed to process log data", e);
        }
    }

    private String nullSafeGet(String value) {
        return value != null ? value : "unknown";
    }
}
