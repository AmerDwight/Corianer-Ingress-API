package tw.amer.cia.core.model.pojo.service.common.user;

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
public class UserDataDto implements Serializable
{
    // General
    @NotBlank(message = "The ACCOUNT must not be empty.")
    @JsonProperty("ACCOUNT")
    private String account;

    @NotBlank(message = "The PASSWORD must not be empty.")
    @JsonProperty("PASSWORD")
    private String password;
}
