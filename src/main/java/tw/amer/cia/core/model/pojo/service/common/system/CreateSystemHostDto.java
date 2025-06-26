package tw.amer.cia.core.model.pojo.service.common.system;


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
public class CreateSystemHostDto implements Serializable
{

    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @JsonProperty("SYSTEM_ENG_NAME")
    private String systemEngName;

    @JsonProperty("SERVICE_LEVEL")
    private Integer serviceLevel;

    @JsonProperty("OWNER")
    private String owner;

    @JsonProperty("SYSTEM_DESC")
    private String systemDesc;

    @JsonProperty("APPLICABLE_FLAG")
    private String applicableFlag;

    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    @JsonProperty("DEPLOYMENT")
    private List<SystemDeploymentDto> deployment;
}
