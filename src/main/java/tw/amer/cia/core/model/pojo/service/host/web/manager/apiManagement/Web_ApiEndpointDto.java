package tw.amer.cia.core.model.pojo.service.host.web.manager.apiManagement;

import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.model.database.ApiEndpointEntity;
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
public class Web_ApiEndpointDto implements Serializable {
    @NotBlank(message = "The API_ID must not be empty.")
    @JsonProperty("API_ID")
    private String apiId;

    @JsonProperty("ENDPOINT_ID")
    private String endpointId;

    @NotBlank(message = "The API_ITF_TYPE must not be empty.")
    @JsonProperty("API_ITF_TYPE")
    private String apiItfType;

    @NotBlank(message = "The API_HOST_URI must not be empty.")
    @JsonProperty("API_HOST_URI")
    private String apiHostUri;

    @NotBlank(message = "The HTTP_METHOD must not be empty.")
    @JsonProperty("HTTP_METHOD")
    private String httpMethod;

    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    public Web_ApiEndpointDto(ApiEndpointEntity src) {
        BeanUtils.copyNonNullProperties(src, this);
    }
}
