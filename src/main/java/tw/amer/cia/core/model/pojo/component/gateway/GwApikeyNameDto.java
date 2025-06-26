package tw.amer.cia.core.model.pojo.component.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GwApikeyNameDto implements Serializable
{
    @JsonProperty("ROLE_ID")
    private String roleId;

    @JsonProperty("KEY_NAME")
    private String keyName;
}
