package tw.amer.cia.core.model.pojo.service.common.api;

import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.model.database.ApiEndpointEntity;
import tw.amer.cia.core.common.utility.BeanUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.net.URISyntaxException;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiEndpointDto implements Serializable
{
    @NotBlank(message = "The API_ITF_TYPE must not be empty.")
    @JsonProperty("API_ITF_TYPE")
    private String apiItfType;

    @NotBlank(message = "The API_HOST_URI must not be empty.")
    @JsonProperty("API_HOST_URI")
    private String apiHostUri;

    @NotBlank(message = "The API_GW_URI must not be empty.")
    @JsonProperty("API_GW_URI")
    private String apiGwUri;

    @NotBlank(message = "The HTTP_METHOD must not be empty.")
    @JsonProperty("HTTP_METHOD")
    private String httpMethod;

    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    public ApiEndpointDto(ApiEndpointEntity src)
    {
        BeanUtils.copyNonNullProperties(src, this);
    }

    public void setApiGwUri(String apiGwUri) throws URISyntaxException
    {
        this.apiGwUri = GeneralSetting.validateUriFormat(apiGwUri);
    }

    public void setApiHostUri(String apiHostUri) throws URISyntaxException
    {
        this.apiHostUri = GeneralSetting.validateUriFormat(apiHostUri);
    }
}
