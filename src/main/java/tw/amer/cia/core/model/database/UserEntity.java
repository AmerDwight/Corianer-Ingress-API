package tw.amer.cia.core.model.database;

import tw.amer.cia.core.common.GeneralSetting;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldNameConstants
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "USER")
@JsonPropertyOrder({"userId", "defaultRolePlay", "lastRolePlay", "userName"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class UserEntity implements Serializable {

    @Id
    @Column(name = "USER_ID", nullable = false, length = 20)
    @JsonProperty("USER_ID")
    private String userId;

    @Column(name = "DEPT_CODE", length = 20)
    @JsonProperty("DEPT_CODE")
    private String deptCode;

    @Column(name = "DEFAULT_ROLE_PLAY", length = 20)
    @JsonProperty("DEFAULT_ROLE_PLAY")
    private String defaultRolePlay;

    @Column(name = "LAST_ROLE_PLAY", length = 20)
    @JsonProperty("LAST_ROLE_PLAY")
    private String lastRolePlay;

    @Column(name = "USER_NAME", length = 40)
    @JsonProperty("USER_NAME")
    private String userName;

    @Column(name = "USER_DESC", length = 240)
    @JsonProperty("USER_DESC")
    private String userDesc;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    @JsonProperty("IS_ACTIVE")
    @Builder.Default
    private String isActive  = GeneralSetting.GENERAL_POSITIVE_STRING;;

    @Column(name = "IS_ADMIN_GROUP", nullable = false, length = 1)
    @JsonProperty("IS_ADMIN_GROUP")
    @Builder.Default
    private String isAdminGroup = GeneralSetting.GENERAL_NEGATIVE_STRING ;

    @Column(name = "LM_TIME", columnDefinition = "TIMESTAMP(6)")
    @JsonProperty("LM_TIME")
    @LastModifiedDate
    private Instant lmTime;

    public UserEntity(String userId, String userName, String deptCode, String userDesc, String isActive) {
        this.userId = userId;
        this.userName = userName;
        this.deptCode = deptCode;
        this.defaultRolePlay = deptCode;
        this.lastRolePlay = deptCode;
        this.userDesc = userDesc;
        this.isActive = StringUtils.equalsIgnoreCase(isActive.trim(), GeneralSetting.GENERAL_POSITIVE_STRING) ?
                GeneralSetting.GENERAL_POSITIVE_STRING : GeneralSetting.GENERAL_NEGATIVE_STRING;
        this.isAdminGroup = GeneralSetting.GENERAL_NEGATIVE_STRING;
    }

}
