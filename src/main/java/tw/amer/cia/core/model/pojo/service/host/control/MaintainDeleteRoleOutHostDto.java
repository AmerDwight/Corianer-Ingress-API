package tw.amer.cia.core.model.pojo.service.host.control;

import tw.amer.cia.core.model.pojo.service.host.control.marker.WebApiTemplateOutDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
public class MaintainDeleteRoleOutHostDto extends WebApiTemplateOutDto implements Serializable
{

    @JsonProperty("DELETED_ROLE")
    private String deletedRole;

}
