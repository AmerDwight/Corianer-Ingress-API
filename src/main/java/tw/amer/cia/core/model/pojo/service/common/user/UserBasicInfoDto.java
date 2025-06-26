package tw.amer.cia.core.model.pojo.service.common.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBasicInfoDto implements Serializable {

    @JsonProperty("STATUS")
    private String status;

    @JsonProperty("AUTH_KEY")
    private String authKey;

    @JsonProperty("WORK_ID")
    private String workId;

    @JsonProperty("USER_NAME")
    private String userName;

    @JsonProperty("DEPT_NO")
    private String deptNo;

    @JsonProperty("EMAIL")
    private String email;

    @JsonProperty("EXT_NO")
    private String extNo;

    @JsonProperty("USER_LOCATION")
    private String userLocation;

    @Builder.Default
    @JsonProperty("IS_ADMIN_GROUP")
    private boolean isAdminGroup = false;

}