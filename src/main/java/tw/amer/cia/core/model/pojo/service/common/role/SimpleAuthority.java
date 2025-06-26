package tw.amer.cia.core.model.pojo.service.common.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleAuthority implements Serializable
{
    @NotBlank(message = "The SYS_NAME must not be empty.")
    @JsonProperty("SYS_NAME")
    private String systemName;

    @NotBlank(message = "The API_NAME must not be empty.")
    @JsonProperty("API_NAME")
    private String apiName;

}
