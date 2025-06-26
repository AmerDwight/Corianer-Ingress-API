package tw.amer.cia.core.model.pojo.service.common.system;

import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.model.database.SystemDpyEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemDeploymentDto implements Serializable {
    @NotBlank(message = "The FAB must not be empty.")
    @JsonProperty("FAB")
    private String fabId;

    @NotBlank(message = "The SCHEME must not be empty.")
    @JsonProperty("SCHEME")
    private String scheme;

    @JsonProperty("TIMEOUT_LIMIT")
    @Builder.Default
    private Integer timeoutLimit = 6;

    @NotBlank(message = "The HOST must not be empty.")
    @JsonProperty("HOST")
    private String systemHost;

    @NotNull(message = "The PORT must not be empty.")
    @JsonProperty("PORT")
    private Integer systemPort;

    @JsonProperty("HEALTH_CHECK")
    private String healthCheckPath;

    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    @JsonProperty("PROXY_REQUIRED")
    private String proxyRequired;

    public SystemDeploymentDto(SystemDpyEntity src) {
        BeanUtils.copyNonNullProperties(src, this);
    }
}
