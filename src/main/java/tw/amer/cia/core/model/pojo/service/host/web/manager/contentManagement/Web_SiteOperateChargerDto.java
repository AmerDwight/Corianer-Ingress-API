package tw.amer.cia.core.model.pojo.service.host.web.manager.contentManagement;

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
public class Web_SiteOperateChargerDto implements Serializable {

    @NotBlank(message = "The FAB_ID must not be empty.")
    @JsonProperty("FAB_ID")
    private String fabId;

    @NotBlank(message = "The USER_ID must not be empty.")
    @JsonProperty("USER_ID")
    private String userId;

    @JsonProperty("USER_NAME")
    private String userName;
}