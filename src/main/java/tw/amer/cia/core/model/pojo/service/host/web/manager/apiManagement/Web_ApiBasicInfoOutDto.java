package tw.amer.cia.core.model.pojo.service.host.web.manager.apiManagement;

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
public class Web_ApiBasicInfoOutDto implements Serializable {
    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @JsonProperty("API_ID")
    private String apiId;

    @JsonProperty("API_NAME")
    private String apiName;

    @JsonProperty("API_ENG_NAME")
    private String apiEngName;

    @JsonProperty("API_DESC")
    private String apiDesc;

    @JsonProperty("API_TYPE")
    private String apiType;

    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    @JsonProperty("APPLICABLE_FLAG")
    private String applicableFlag;

    @JsonProperty("OWNER")
    private String owner;
}