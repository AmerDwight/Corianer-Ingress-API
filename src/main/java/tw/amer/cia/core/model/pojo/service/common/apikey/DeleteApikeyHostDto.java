package tw.amer.cia.core.model.pojo.service.common.apikey;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteApikeyHostDto implements Serializable
{

    @JsonProperty("ROLE_ID")
    private String roleId;

    @JsonProperty("APIKEY")
    private String apikeyId;

}
