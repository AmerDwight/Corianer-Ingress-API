package tw.amer.cia.core.model.database;

import tw.amer.cia.core.model.database.compositeId.SystemDpyEntityId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.ColumnDefault;
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
@Table(name = "SYSTEM_DPY")
@IdClass(SystemDpyEntityId.class)
@JsonPropertyOrder({"systemId", "fabId", "systemHost", "systemPort", "proxyRequired", "scheme", "healthCheckPath", "activeStatus", "lmUser", "lmTime", "createTime"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class SystemDpyEntity implements Serializable {

    @Id
    @Column(name = "SYSTEM_ID", nullable = false, length = 20)
    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @Id
    @Column(name = "FAB_ID", nullable = false, length = 20)
    @JsonProperty("FAB_ID")
    private String fabId;

    @Column(name = "SYSTEM_HOST", length = 40)
    @JsonProperty("SYSTEM_HOST")
    private String systemHost;

    @Column(name = "SYSTEM_PORT")
    @JsonProperty("SYSTEM_PORT")
    private Integer systemPort;

    @Column(name = "PROXY_REQUIRED", nullable = false, length = 1)
    @ColumnDefault("'N'")
    @JsonProperty("PROXY_REQUIRED")
    private String proxyRequired;

    @Column(name = "SCHEME", length = 20)
    @JsonProperty("SCHEME")
    private String scheme;

    @Column(name = "TIMEOUT_LIMIT")
    @JsonProperty("TIMEOUT_LIMIT")
    @Builder.Default
    private Integer timeoutLimit = 6;

    @Column(name = "HEALTH_CHECK_PATH", length = 100)
    @JsonProperty("HEALTH_CHECK_PATH")
    private String healthCheckPath;

    @Column(name = "ACTIVE_STATUS", nullable = false, length = 20)
    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

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
