package tw.amer.cia.core.model.pojo.service.host.web.manager.apiManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_CreatePureApiDto implements Serializable {
    @NotBlank(message = "The SYSTEM_ID must not be empty.")
    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @NotBlank(message = "The API_NAME must not be empty.")
    @Pattern(regexp = "^[\\u4e00-\\u9fffa-zA-Z0-9\\-]+$", message = "The API_NAME can only contain Chinese characters, English letters, numbers and hyphens, no spaces allowed.")
    @JsonProperty("API_NAME")
    private String apiName;

    @NotBlank(message = "The API_ENG_NAME must not be empty.")
    @Pattern(regexp = "^[A-Z][a-zA-Z0-9]*$", message = "The API_ENG_NAME must be in UpperCamelCase format with only letters and numbers, no spaces or special characters allowed.")
    @JsonProperty("API_ENG_NAME")
    private String apiEngName;

    @NotBlank(message = "The API_TYPE must not be empty.")
    @JsonProperty("API_TYPE")
    private String apiType;

    @JsonProperty("API_DESC")
    private String apiDesc;

    @NotBlank(message = "The APPLICABLE_FLAG must not be empty.")
    @JsonProperty("APPLICABLE_FLAG")
    private String applicableFlag;

    @NotBlank(message = "The OWNER must not be empty.")
    @JsonProperty("OWNER")
    private String owner;

    @NotBlank(message = "The ACTIVE_STATUS must not be empty.")
    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

}
