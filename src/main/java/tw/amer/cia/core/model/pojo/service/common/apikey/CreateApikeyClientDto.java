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
public class CreateApikeyClientDto implements Serializable
{
    @JsonProperty("FAB_ID")
    private String fabId;

    @JsonProperty("ROLE_ID")
    private String roleId;

    @JsonProperty("KEY_NAME")
    private String keyName;

    @JsonProperty("API_ID_LIST")
    private List<String> apiIdList;
}
