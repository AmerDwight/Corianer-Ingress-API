package tw.amer.cia.core.model.pojo.service.common.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.amer.cia.core.model.database.ApiEndpointEntity;
import tw.amer.cia.core.model.database.ApiEntity;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrUpdateApiEndpointDto implements Serializable {

    @JsonProperty("API")
    private ApiEntity apiEntity;

    @JsonProperty("API_ENDPOINT")
    private List<ApiEndpointEntity> apiEndpointEntities;

}
