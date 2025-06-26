package tw.amer.cia.core.model.pojo.service.common.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleFabScopeDto
{
    @JsonProperty("FAB")
    private String fabId;

    @JsonProperty("AUTHORITIES")
    private List<SimpleAuthorityDto> authorities;
}
