package tw.amer.cia.core.model.database;

import tw.amer.cia.core.model.database.compositeId.RoleAuthoroityEntityId;
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
@Table(name = "ROLE_AUTHORITY")
@IdClass(RoleAuthoroityEntityId.class)
@JsonPropertyOrder({"roleId", "apiId", "fabId", "applyFormNumber", "lmUser", "lmTime"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class RoleAuthorityEntity implements Serializable
{

    @Id
    @Column(name = "ROLE_ID", nullable = false, length = 20)
    @JsonProperty("ROLE_ID")
    private String roleId;

    @Id
    @Column(name = "API_ID", nullable = false, length = 20)
    @JsonProperty("API_ID")
    private String apiId;

    @Id
    @Column(name = "FAB_ID", nullable = false, length = 20)
    @JsonProperty("FAB_ID")
    private String fabId;

    @Column(name = "APPLY_FORM_NUMBER", length = 40)
    @JsonProperty("APPLY_FORM_NUMBER")
    private String applyFormNumber;

    @Column(name = "LM_USER", length = 40)
    @JsonProperty("LM_USER")
    @LastModifiedBy
    private String lmUser;

    @Column(name = "LM_TIME", columnDefinition = "TIMESTAMP(6)")
    @JsonProperty("LM_TIME")
    @LastModifiedDate
    private Instant lmTime;

}
