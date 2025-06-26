package tw.amer.cia.core.model.pojo.service.common.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccessBasicInfoDto {

    @JsonProperty("USER_INFO")
    ApiUserInfoDto apiUserInfoDto;

}
