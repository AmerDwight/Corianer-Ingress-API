package tw.amer.cia.core.model.database;

import tw.amer.cia.core.model.database.compositeId.FabProxyEntityId;
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
@Table(name = "FAB_PROXY")
@IdClass(FabProxyEntityId.class)
@JsonPropertyOrder({"FAB_ID", "PROXY_ID"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class FabProxyEntity implements Serializable
{
    @Id
    @Column(name = "FAB_ID", nullable = false, length = 20)
    @JsonProperty("FAB_ID")
    private String fabId;

    @Id
    @Column(name = "PROXY_ID", nullable = false, length = 20)
    @JsonProperty("PROXY_ID")
    private String proxyId;
}
