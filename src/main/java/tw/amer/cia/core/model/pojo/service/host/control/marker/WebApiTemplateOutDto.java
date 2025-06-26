package tw.amer.cia.core.model.pojo.service.host.control.marker;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
public class WebApiTemplateOutDto implements Serializable
{
    @JsonProperty("RTN_CODE")
    @Builder.Default
    private String rtnCode = "0000000";
    @JsonProperty("RTN_MESSAGE")
    @Builder.Default
    private String rtnMessage = "Action Success.";
}
