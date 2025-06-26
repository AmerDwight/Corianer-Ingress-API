package tw.amer.cia.core.model.pojo.service.common.user;

import tw.amer.cia.core.model.database.RoleEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginInfoDto {

    @JsonProperty("USER_INFO")
    private UserBasicInfoDto userInfo;

    @JsonProperty("DEFAULT_ROLE")
    private RoleEntity defaultRole;

    @JsonProperty("ROLE_LIST")
    private List<RoleEntity> roleList;
}
