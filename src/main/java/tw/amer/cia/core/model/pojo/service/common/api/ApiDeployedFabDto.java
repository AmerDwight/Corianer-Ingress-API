package tw.amer.cia.core.model.pojo.service.common.api;

import tw.amer.cia.core.model.database.ApiDpyEntity;
import tw.amer.cia.core.common.utility.BeanUtils;
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
public class ApiDeployedFabDto implements Serializable
{
    @JsonProperty("FAB")
    @NotBlank(message = "The FAB must not be empty.")
    private String fabId;

    public ApiDeployedFabDto(ApiDpyEntity apiDpyEntity)
    {
        BeanUtils.copyNonNullProperties(apiDpyEntity, this);
    }
}
