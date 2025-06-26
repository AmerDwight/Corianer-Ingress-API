package tw.amer.cia.core.model.pojo.service.host.web.manager.contentManagement;

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
public class Web_SystemItemDto implements Serializable {
    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    @JsonProperty("API_COUNT")
    private long apiCount;

}
