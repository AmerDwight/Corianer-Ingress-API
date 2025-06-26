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
@Table(name = "API")
@JsonPropertyOrder({"apiId", "apiName", "systemId", "apiType", "activeStatus", "applicableFlag", "owner", "lmUser", "lmTime", "createTime"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class ApiEntity implements Serializable
{

    @Id
    @Column(name = "API_ID", nullable = false, length = 20)
    @JsonProperty("API_ID")
    private String apiId;

    @Column(name = "API_NAME", length = 100)
    @JsonProperty("API_NAME")
    private String apiName;

    @Column(name = "API_ENG_NAME", length = 100)
    @JsonProperty("API_ENG_NAME")
    private String apiEngName;

    @Column(name = "SYSTEM_ID", nullable = false, length = 20)
    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @Column(name = "API_DESC", length = 240)
    @JsonProperty("API_DESC")
    private String apiDesc;

    @Column(name = "API_TYPE", length = 40)
    @JsonProperty("API_TYPE")
    private String apiType;

    @Column(name = "ACTIVE_STATUS", nullable = false, length = 20)
    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    @Column(name = "APPLICABLE_FLAG", nullable = false, length = 1)
    @JsonProperty("APPLICABLE_FLAG")
    private String applicableFlag;

    @Column(name = "OWNER", length = 40)
    @JsonProperty("OWNER")
    private String owner;

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
