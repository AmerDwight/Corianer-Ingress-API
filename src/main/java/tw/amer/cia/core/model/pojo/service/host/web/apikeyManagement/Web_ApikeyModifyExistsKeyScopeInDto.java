package tw.amer.cia.core.model.pojo.service.host.web.apikeyManagement;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_ApikeyModifyExistsKeyScopeInDto implements Serializable {

    @JsonProperty("ENABLE_SCOPES")
    List<String> enableScopeList;

    @JsonProperty("DISABLE_SCOPES")
    List<String> disableScopeList;

}
