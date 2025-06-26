package tw.amer.cia.core.model.database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldNameConstants
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "SYSTEM_PROXY")
@JsonPropertyOrder({"PROXY_ID", "SCHEME", "PROXY_HOST", "PROXY_PORT", "AUTHENTICATE_FLAG", "PROXY_ACCOUNT", "PROXY_PASSWORD", "ACTIVE_STATUS"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class SystemProxyEntity implements Serializable
{

    @Id
    @Column(name = "PROXY_ID", nullable = false, length = 20)
    @JsonProperty("PROXY_ID")
    private String proxyId;

    @Column(name = "SCHEME", length = 20)
    @JsonProperty("SCHEME")
    private String scheme;

    @Column(name = "PROXY_HOST", nullable = false, length = 40)
    @JsonProperty("PROXY_HOST")
    private String proxyHost;

    @Column(name = "PROXY_PORT")
    @JsonProperty("PROXY_PORT")
    private Integer proxyPort;

    @Column(name = "AUTHENTICATE_FLAG", nullable = false, length = 1)
    @JsonProperty("AUTHENTICATE_FLAG")
    private String authenticateFlag;

    @Column(name = "PROXY_ACCOUNT", length = 20)
    @JsonProperty("PROXY_ACCOUNT")
    private String proxyAccount;

    @Column(name = "PROXY_PASSWORD", length = 20)
    @JsonProperty("PROXY_PASSWORD")
    private String proxyPassword;

    @Column(name = "ACTIVE_STATUS", nullable = false, length = 20)
    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;
}
