package tw.amer.cia.core.model.database;

import tw.amer.cia.core.model.database.compositeId.RoleAuthApplyEntityId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
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
@Table(name = "ROLE_AUTH_APPLY")
@IdClass(RoleAuthApplyEntityId.class)
@JsonPropertyOrder({"applyFormId", "roleId", "formStatus", "applicant", "createTime"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class RoleAuthApplyEntity implements Serializable
{

    @Id
    @Column(name = "APPLY_FORM_ID", nullable = false, length = 20)
    @JsonProperty("APPLY_FORM_ID")
    private String applyFormId;

    @Id
    @Column(name = "ROLE_ID", nullable = false, length = 20)
    @JsonProperty("ROLE_ID")
    private String roleId;

    @Column(name = "FORM_STATUS", length = 20)
    @JsonProperty("FORM_STATUS")
    private String formStatus;

    @Column(name = "APPLICANT", length = 20)
    @JsonProperty("APPLICANT")
    private String applicant;

    @Column(name = "CREATE_TIME", columnDefinition = "TIMESTAMP(6)")
    @JsonProperty("CREATE_TIME")
    @CreatedDate
    private Instant createTime;

    @Column(name = "LM_TIME", columnDefinition = "TIMESTAMP(6)")
    @JsonProperty("LM_TIME")
    @LastModifiedDate
    private Instant lmTime;

}
