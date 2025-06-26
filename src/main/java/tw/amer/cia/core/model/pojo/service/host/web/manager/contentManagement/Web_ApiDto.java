package tw.amer.cia.core.model.pojo.service.host.web.manager.contentManagement;

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
public class Web_ApiDto implements Serializable {
    @JsonProperty("API_ID")
    private String apiId;

    @JsonProperty("API_NAME")
    private String apiName;

    @JsonProperty("API_TYPE")
    private String apiType;

    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    @JsonProperty("SITE_LIST")
    private List<String> site;

}