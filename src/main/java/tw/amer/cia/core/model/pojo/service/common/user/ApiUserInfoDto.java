package tw.amer.cia.core.model.pojo.service.common.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiUserInfoDto {

    @NotBlank(message = "The USER_ID must not be empty.")
    @JsonProperty("USER_ID")
    String userId;

    @NotBlank(message = "The ROLE_ID must not be empty.")
    @JsonProperty("ROLE_ID")
    String roleId;

}
