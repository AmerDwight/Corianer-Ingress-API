package tw.amer.cia.core.model.database;

import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.model.database.compositeId.RoleUserEntityId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.LastModifiedBy;
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
@Table(name = "ROLE_USER")
@IdClass(RoleUserEntityId.class)
@JsonPropertyOrder({"userId", "roleId", "lmUser", "lmTime"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class RoleUserEntity implements Serializable
{
    @Id
    @Column(name = "USER_ID", nullable = false, length = 20)
    @JsonProperty("USER_ID")
    private String userId;

    @Id
    @Column(name = "ROLE_ID", nullable = false, length = 20)
    @JsonProperty("ROLE_ID")
    private String roleId;

    @Column(name = "IS_UI_VISIBLE", nullable = false, length = 1)
    @JsonProperty("IS_UI_VISIBLE")
    @Builder.Default
    private String isUiVisible = GeneralSetting.GENERAL_POSITIVE_STRING;

    @Column(name = "LM_USER", length = 40)
    @JsonProperty("LM_USER")
    @LastModifiedBy
    private String lmUser;

    @Column(name = "LM_TIME", columnDefinition = "TIMESTAMP(6)")
    @JsonProperty("LM_TIME")
    @LastModifiedDate
    private Instant lmTime;

}
