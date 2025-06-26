package tw.amer.cia.core.model.pojo.service.host.web.manager.apiManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_ApiUpdateDeployDto implements Serializable {
    @NotBlank(message = "The API_ID must not be empty.")
    @JsonProperty("API_ID")
    private String apiId;

    @JsonProperty("DEPLOYMENT")
    private List<Web_ApiDeployedBySiteDto> deployedBySiteDtoList;
}
