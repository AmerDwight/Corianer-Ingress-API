package tw.amer.cia.core.model.database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
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
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EXTERNAL_SYSTEM_CONFIG")
@JsonPropertyOrder({"extEntityId", "extEntityKey", "lmTime"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class ExternalSystemConfigEntity implements Serializable
{

    @Id
    @Column(name = "EXT_SYSTEM_ID", nullable = false, length = 40)
    @JsonProperty("EXT_SYSTEM_ID")
    private String extSystemId;

    @Column(name = "EXT_SYSTEM_KEY", length = 20)
    @JsonProperty("EXT_SYSTEM_KEY")
    private String extSystemKey;

    @Column(name = "LM_TIME", columnDefinition = "TIMESTAMP(6)")
    @JsonProperty("LM_TIME")
    @LastModifiedDate
    private Instant lmTime;
}