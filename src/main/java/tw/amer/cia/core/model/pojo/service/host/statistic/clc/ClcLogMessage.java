package tw.amer.cia.core.model.pojo.service.host.statistic.clc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClcLogMessage implements Serializable {
    @Builder.Default
    private String appName = "CIA";
    private Map<String, String> labels;
    private String message;
    private Instant timestamp;
}