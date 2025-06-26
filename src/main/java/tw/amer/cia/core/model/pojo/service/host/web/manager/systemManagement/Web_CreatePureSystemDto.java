package tw.amer.cia.core.model.pojo.service.host.web.manager.systemManagement;

import tw.amer.cia.core.common.GeneralSetting;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_CreatePureSystemDto implements Serializable {

    @NotBlank(message = "The SYSTEM_NAME must not be empty.")
    @Pattern(regexp = "^[\\w-]+$", message = "The SYSTEM_NAME must contain only alphanumeric characters, dash and underscores, case sensitive.")
    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @NotBlank(message = "The SYSTEM_NAME must not be empty.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "The SYSTEM_ENG_NAME must contain only alphanumeric characters and numbers, case sensitive.")
    @JsonProperty("SYSTEM_ENG_NAME")
    private String systemEngName;

    @JsonProperty("SERVICE_LEVEL")
    private Integer serviceLevel;

    @JsonProperty("SYSTEM_DESC")
    private String systemDesc;

    @JsonProperty("OWNER")
    private String owner;

    @JsonProperty("ACTIVE_STATUS")
    @Builder.Default
    private String activeStatus = GeneralSetting.GENERAL_ACTIVE_STATUS_ACTIVE;

    @JsonProperty("APPLICABLE_FLAG")
    @Builder.Default
    private String applicableFlag = GeneralSetting.GENERAL_POSITIVE_STRING;
}