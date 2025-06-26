package tw.amer.cia.core.model.pojo.service.host.web.manager.apiManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_ApiBasicInfoUpdateDto implements Serializable {
    @NotBlank(message = "The SYSTEM_ID must not be empty.")
    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @NotBlank(message = "The API_ID must not be empty.")
    @JsonProperty("API_ID")
    private String apiId;

    @JsonProperty("API_DESC")
    private String apiDesc;

    @NotBlank(message = "The API_TYPE must not be empty.")
    @JsonProperty("API_TYPE")
    private String apiType;

    @NotBlank(message = "The ACTIVE_STATUS must not be empty.")
    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    @NotBlank(message = "The APPLICABLE_FLAG must not be empty.")
    @JsonProperty("APPLICABLE_FLAG")
    private String applicableFlag;

    @NotBlank(message = "The OWNER must not be empty.")
    @JsonProperty("OWNER")
    private String owner;
}