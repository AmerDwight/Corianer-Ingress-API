package tw.amer.cia.core.model.pojo.service.common.system;

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
public class DeleteSystemHostDto implements Serializable
{
    @JsonProperty("SYSTEM_NAME")
    private String systemName;
}
