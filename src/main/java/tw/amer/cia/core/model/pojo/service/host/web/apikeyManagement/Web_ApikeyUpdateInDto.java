package tw.amer.cia.core.model.pojo.service.host.web.apikeyManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_ApikeyUpdateInDto implements Serializable {

    @JsonProperty("APIKEY_ID")
    String apikeyId;

    @JsonProperty("FAB_ID_API_MAP")
    Map<String, List<String>> fabIdApiListMap;
}
