package tw.amer.cia.core.model.pojo.component.gateway.apisix.item.route.plugin;

import tw.amer.cia.core.model.pojo.component.gateway.apisix.item.MetaDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CiaRequestVerifyPluginDto implements Serializable
{

    @JsonProperty("_meta")
    @Builder.Default
    private MetaDto meta = MetaDto.builder().build();

    @NotBlank(message = "The verify_source must not be empty.")
    @JsonProperty("verify_source")
    private String verifySource;

    @NotBlank(message = "The verify_field must not be empty.")
    @JsonProperty("verify_field")
    private String verifyField;

    @NotBlank(message = "The verify_value must not be empty.")
    @JsonProperty("verify_value")
    private String verifyValue;
}
