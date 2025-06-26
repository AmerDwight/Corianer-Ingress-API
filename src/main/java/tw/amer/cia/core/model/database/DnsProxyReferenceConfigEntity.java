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
@Table(name = "DNS_PROXY_REFERENCE_CONFIG")
@JsonPropertyOrder({"HOSTNAME", "PROXY_REF"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class DnsProxyReferenceConfigEntity implements Serializable
{

    @Id
    @Column(name = "HOSTNAME", nullable = false, length = 40)
    @JsonProperty("HOSTNAME")
    private String hostname;

    @Column(name = "PROXY_REF", nullable = false, length = 40)
    @JsonProperty("PROXY_REF")
    private String proxyRef;


}
