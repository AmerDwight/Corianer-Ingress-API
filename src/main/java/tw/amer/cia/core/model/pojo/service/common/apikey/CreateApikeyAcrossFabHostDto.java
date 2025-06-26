package tw.amer.cia.core.model.pojo.service.common.apikey;

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
public class CreateApikeyAcrossFabHostDto implements Serializable
{
    @JsonProperty("ROLE_ID")
    private String roleId;

    @JsonProperty("KEY_NAME")
    private String keyName;

    @JsonProperty("KEY_DESC")
    private String keyDesc;

    @JsonProperty("ON_GRANT_FAB_API_LIST")
    private Map<String,List<String>> fabIdApiListMap;

}
