package tw.amer.cia.core.model.pojo.service.common.apikey;

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
public class SimplePermissionWithIdDto implements Serializable
{
    @NotBlank(message = "The SYS_NAME must not be empty.")
    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @NotBlank(message = "The SYS_NAME must not be empty.")
    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @NotBlank(message = "The API_ID must not be empty.")
    @JsonProperty("API_ID")
    private String apiId;

    @NotBlank(message = "The API_NAME must not be empty.")
    @JsonProperty("API_NAME")
    private String apiName;

}
