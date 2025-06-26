package tw.amer.cia.core.model.pojo.service.host.control;

import tw.amer.cia.core.model.pojo.service.common.system.SystemDeploymentDto;
import tw.amer.cia.core.model.pojo.service.host.control.marker.WebApiTemplateOutDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder
public class MaintainRetrieveSystemOutHostDto extends WebApiTemplateOutDto implements Serializable
{

    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @JsonProperty("SERVICE_LEVEL")
    private Integer serviceLevel;

    @JsonProperty("OWNER")
    private String owner;

    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    @JsonProperty("DEPLOYMENT")
    private List<SystemDeploymentDto> deployment;

}
