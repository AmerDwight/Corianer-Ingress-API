package tw.amer.cia.core.model.pojo.service.host.web.manager.systemManagement;

import tw.amer.cia.core.common.GeneralSetting;
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
public class Web_UpdateBasicSystemInfoDto implements Serializable {

    @NotBlank(message = "The SYSTEM_ID must not be empty.")
    @JsonProperty("SYSTEM_ID")
    private String systemId;

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