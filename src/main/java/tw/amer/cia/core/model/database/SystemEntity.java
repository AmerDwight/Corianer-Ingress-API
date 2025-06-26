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
@Table(name = "SYSTEM")
@JsonPropertyOrder({"systemId", "systemName", "serviceLevel", "owner", "activeStatus", "applicableFlag", "lmUser", "lmTime", "createTime"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class SystemEntity implements Serializable
{

    @Id
    @Column(name = "SYSTEM_ID", nullable = false, length = 20)
    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @Column(name = "SYSTEM_NAME", unique = true, length = 100)
    @JsonProperty("SYSTEM_NAME")
    private String systemName;

    @Column(name = "SYSTEM_ENG_NAME", unique = true, length = 100)
    @JsonProperty("SYSTEM_ENG_NAME")
    private String systemEngName;

    @Column(name = "SYSTEM_DESC", length = 240)
    @JsonProperty("SYSTEM_DESC")
    private String systemDesc;

    @Column(name = "SERVICE_LEVEL")
    @JsonProperty("SERVICE_LEVEL")
    private Integer serviceLevel;

    @Column(name = "OWNER", length = 40)
    @JsonProperty("OWNER")
    private String owner;

    @Column(name = "ACTIVE_STATUS", nullable = false, length = 20)
    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    @Column(name = "APPLICABLE_FLAG", nullable = false, length = 1)
    @JsonProperty("APPLICABLE_FLAG")
    private String applicableFlag;

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
