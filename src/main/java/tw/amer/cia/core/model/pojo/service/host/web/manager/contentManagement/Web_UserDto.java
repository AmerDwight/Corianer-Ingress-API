package tw.amer.cia.core.model.pojo.service.host.web.manager.contentManagement;

import tw.amer.cia.core.model.database.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Web_UserDto implements Serializable {
    @JsonProperty("USER_ID")
    private String userId;

    @JsonProperty("USER_NAME")
    private String userName;

    public static List<Web_UserDto> importFromUserTable(Collection<UserEntity> users) {
        return users.stream().map(cUsrUsr -> {
            return Web_UserDto.builder().userId(cUsrUsr.getUserId()).userName(cUsrUsr.getUserName()).build();
        }).collect(Collectors.toList());
    }
}