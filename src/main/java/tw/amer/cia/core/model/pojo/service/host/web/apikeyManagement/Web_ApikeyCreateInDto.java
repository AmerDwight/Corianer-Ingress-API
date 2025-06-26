package tw.amer.cia.core.model.pojo.service.host.web.apikeyManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_ApikeyCreateInDto implements Serializable {

    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "The KEY_NAME must only contain alphanumeric characters, underscores, case sensitive.")
    @JsonProperty("NEW_APIKEY_NAME")
    String newApikeyName;

    @JsonProperty("NEW_APIKEY_DESC")
    String newApikeyDesc;

    @JsonProperty("FAB_ID_API_MAP")
    Map<String, List<String>> fabIdApiListMap;
}
