package tw.amer.cia.core.component.functional.statistic.clc;


import tw.amer.cia.core.component.structural.annotation.HostComponent;
import tw.amer.cia.core.component.structural.httpClient.HttpRequestSender;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.pojo.service.host.statistic.clc.ClcLogMessage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@HostComponent
public class ClcMessageSender extends HttpRequestSender {

    @Value("${coriander-ingress-api.host.clc.https:false}")
    @Setter
    private boolean isClcUseHttps;

    @Value("${coriander-ingress-api.host.clc.host:localhost}")
    @Setter
    private String clcHost;

    @Value("${coriander-ingress-api.host.clc.port:3100}")
    @Setter
    private Integer clcPort;

    @Value("${coriander-ingress-api.host.clc.batch-size:100}")
    @Setter
    private Integer clcMessageBatchSize;

    private final String BATCH_PUSH_ENDPOINT = "/data/collect/list/log";

    // 存儲待發送的消息
    private final List<ClcLogMessage> messageBuffer = Collections.synchronizedList(new ArrayList<>());

    public ClcMessageSender() {
        super(false);
    }

    public ClcMessageSender(boolean _enableProxy) {
        super(_enableProxy);
    }

    /**
     * 將日誌消息添加到緩衝區
     */
    public void addMessage(ClcLogMessage message) throws DataSourceAccessException {
        messageBuffer.add(message);
        if (messageBuffer.size() >= clcMessageBatchSize) {
            flush();
        }
    }

    /**
     * 定時執行刷新操作
     * fixedRate: 固定間隔執行
     * initialDelay: 啟動後延遲執行的時間
     */
    @Scheduled(fixedRateString = "${coriander-ingress-api.setting.host.clc.flush-interval-ms:300000}",
            initialDelay = 300000)
    public void scheduledFlush() {
        try {
            if (!messageBuffer.isEmpty()) {
                log.debug("Scheduled flush triggered. Buffer size: {}", messageBuffer.size());
                flush();
            }
        } catch (Exception e) {
            log.error("Error during scheduled flush: {}", e.getMessage(), e);
        }
    }

    /**
     * 強制發送所有緩衝區中的消息
     */
    public void flush() throws DataSourceAccessException {
        if (messageBuffer.isEmpty()) {
            return;
        }

        try {
            sendToClc(new ArrayList<>(messageBuffer));
        } catch (Exception e) {
            throw e;
        } finally {
            messageBuffer.clear();
        }
    }

    /**
     * 將消息發送到 Cia Log Center
     */
    private void sendToClc(Collection<ClcLogMessage> messages) throws DataSourceAccessException {
        String url = buildUrl(isClcUseHttps, clcHost, clcPort, BATCH_PUSH_ENDPOINT);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> httpEntity = new HttpEntity<>(messages, headers);

        ResponseEntity<String> response = sendHttpCommandWithFaultTolerance(
                httpEntity,
                HttpMethod.POST,
                url
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to send logs to CIA Log Center. Status: {}, Response: {}",
                    response.getStatusCode(),
                    response.getBody()
            );
            throw DataSourceAccessException.createExceptionForHttp(
                    response.getStatusCode(), response.getBody());
        }
    }
    @PreDestroy
    public void destroy() throws DataSourceAccessException {
        flush(); // 確保關閉前發送所有待處理的消息
    }
}
