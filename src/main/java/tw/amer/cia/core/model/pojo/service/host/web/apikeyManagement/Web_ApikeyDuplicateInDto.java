package tw.amer.cia.core.model.pojo.service.host.web.apikeyManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_ApikeyDuplicateInDto implements Serializable {

    @NotBlank(message = "SOURCE_APIKEY_ID is required.")
    @JsonProperty("SOURCE_APIKEY_ID")
    String sourceApikeyId;

    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "The KEY_NAME must only contain alphanumeric characters, underscores, case sensitive.")
    @JsonProperty("NEW_APIKEY_NAME")
    String newApikeyName;

}
