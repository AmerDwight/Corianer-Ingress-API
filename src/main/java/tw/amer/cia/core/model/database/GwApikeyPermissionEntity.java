package tw.amer.cia.core.model.database;

import tw.amer.cia.core.model.database.compositeId.GwApikeyPermissionEntityId;
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
@Table(name = "GW_APIKEY_PERMISSION")
@IdClass(GwApikeyPermissionEntityId.class)
@JsonPropertyOrder({"apikeyId", "apiId", "fabId", "lmUser", "lmTime", "createTime"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class GwApikeyPermissionEntity implements Serializable
{

    @Id
    @Column(name = "APIKEY_ID", nullable = false, length = 20)
    @JsonProperty("APIKEY_ID")
    private String apikeyId;

    @Id
    @Column(name = "API_ID", nullable = false, length = 20)
    @JsonProperty("API_ID")
    private String apiId;

    @Id
    @Column(name = "FAB_ID", nullable = false, length = 20)
    @JsonProperty("FAB_ID")
    private String fabId;

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
