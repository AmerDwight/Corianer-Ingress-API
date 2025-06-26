package tw.amer.cia.core.model.pojo.service.host.control;

import tw.amer.cia.core.model.pojo.service.host.control.marker.WebApiTemplateOutDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
public class MaintainRetrieveApikeyOutHostDto extends WebApiTemplateOutDto implements Serializable
{

    @JsonProperty("FAB")
    private String fabId;

    @JsonProperty("APIKEY")
    private String apikey;

    @JsonProperty("PERMISSIONS")
    private Object permissions;

}
