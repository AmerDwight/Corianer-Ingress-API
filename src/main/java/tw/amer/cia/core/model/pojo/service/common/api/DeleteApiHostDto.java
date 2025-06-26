package tw.amer.cia.core.model.pojo.service.common.api;

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
public class DeleteApiHostDto implements Serializable
{
    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @JsonProperty("API_NAME")
    private String apiName;

}
