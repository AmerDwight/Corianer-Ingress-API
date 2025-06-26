package tw.amer.cia.core.model.pojo.service.common.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleDeviceHostDto implements Serializable
{
    @NotBlank(message = "DEVICE_ID must not be empty.")
    @JsonProperty("DEVICE_ID")
    String deviceId;

    @NotBlank(message = "ROLE_ID must be assigned.")
    @JsonProperty("ROLE_ID")
    String roleId;

    @NotBlank(message = "FAB_ID must not be empty.")
    @JsonProperty("FAB_ID")
    String fabId;

    @JsonProperty("DEVICE_NAME")
    String deviceName;

    @JsonProperty("DEVICE_IP")
    String deviceIp;

    @JsonProperty("IS_ACTIVE")
    String isActive;

    @JsonProperty("DEVICE_DESC")
    String deviceDesc;

}
