package tw.amer.cia.core.model.pojo.service.host.control;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ListApikeyPermissionDto implements Serializable
{
    @JsonProperty("API_TYPE")
    private String apiType;

    @JsonProperty("SYS_NAME")
    private String systemName;

    @JsonProperty("API_NAME")
    private String apiName;
}
