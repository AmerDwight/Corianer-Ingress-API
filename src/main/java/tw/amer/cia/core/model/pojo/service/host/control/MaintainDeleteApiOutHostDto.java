package tw.amer.cia.core.model.pojo.service.host.control;

import tw.amer.cia.core.model.pojo.service.host.control.marker.WebApiTemplateOutDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
public class MaintainDeleteApiOutHostDto extends WebApiTemplateOutDto implements Serializable
{

    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @JsonProperty("DELETED_API")
    private String deletedApi;

}
