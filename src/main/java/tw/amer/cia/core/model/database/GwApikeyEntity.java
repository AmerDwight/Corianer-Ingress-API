package tw.amer.cia.core.model.database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
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
@Table(name = "GW_APIKEY")
@JsonPropertyOrder({"apikeyId", "roleId", "keyName", "keyDesc", "lmUser", "lmTime", "createTime"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class GwApikeyEntity implements Serializable
{

    @Id
    @Column(name = "APIKEY_ID", nullable = false, length = 20)
    @JsonProperty("APIKEY_ID")
    private String apikeyId;

    @Column(name = "ROLE_ID", nullable = false, length = 20)
    @JsonProperty("ROLE_ID")
    private String roleId;

    @Column(name = "KEY_NAME", length = 40)
    @JsonProperty("KEY_NAME")
    private String keyName;

    @Column(name = "KEY_DESC", length = 240)
    @JsonProperty("KEY_DESC")
    private String keyDesc;

    @Column(name = "IS_UI_VISIBLE", nullable = false, length = 1)
    @JsonProperty("IS_UI_VISIBLE")
    @Builder.Default
    private String isUiVisible = "Y";

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    @JsonProperty("IS_ACTIVE")
    private String isActive;

    @Column(name = "LM_USER", length = 40)
    @JsonProperty("LM_USER")
    @LastModifiedBy
    private String lmUser;

    @Column(name = "LM_TIME", columnDefinition = "TIMESTAMP(6)")
    @JsonProperty("LM_TIME")
    @LastModifiedDate
    private Instant lmTime;

    @Column(name = "CREATE_TIME", columnDefinition = "TIMESTAMP(6)")
    @JsonProperty("CREATE_TIME")
    @CreatedDate
    private Instant createTime;

}
