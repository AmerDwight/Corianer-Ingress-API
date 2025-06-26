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
@Table(name = "FAB_SIGN_OFF_CONFIG")
@JsonPropertyOrder({"FAB_ID", "SITE_OWNER_ID"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class FabSignOffConfigEntity implements Serializable
{

    @Id
    @Column(name = "FAB_ID", nullable = false, length = 20)
    @JsonProperty("FAB_ID")
    private String fabId;

    @Column(name = "SITE_MANAGER_ID", nullable = false, length = 20)
    @JsonProperty("SITE_MANAGER_ID")
    private String siteManagerId;


}
