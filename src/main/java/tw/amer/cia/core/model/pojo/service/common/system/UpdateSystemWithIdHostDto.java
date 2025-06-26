package tw.amer.cia.core.model.pojo.service.common.system;

import tw.amer.cia.core.common.GeneralSetting;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSystemWithIdHostDto implements Serializable
{
    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @JsonProperty("SYSTEM_ENG_NAME")
    private String systemEngName;

    @JsonProperty("SYSTEM_DESC")
    private String systemDesc;

    @JsonProperty("SERVICE_LEVEL")
    private Integer serviceLevel;

    @JsonProperty("OWNER")
    private String owner;

    @JsonProperty("ACTIVE_STATUS")
    @Builder.Default
    private String activeStatus = GeneralSetting.GENERAL_ACTIVE_STATUS_ACTIVE;

    @JsonProperty("APPLICABLE_FLAG")
    private String applicableFlag;

    @JsonProperty("DEPLOYMENT")
    private List<SystemDeploymentDto> deployment;

}
