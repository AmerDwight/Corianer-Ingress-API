package tw.amer.cia.core.model.pojo.service.common.apikey;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApikeyHostDto implements Serializable
{

    @JsonProperty("FAB")
    private String fabId;

    @JsonProperty("ROLE_ID")
    private String roleId;

    @JsonProperty("APIKEY")
    private String apikeyId;

    @JsonProperty("API_ID_LIST")
    private List<String> apiIdList;

}
