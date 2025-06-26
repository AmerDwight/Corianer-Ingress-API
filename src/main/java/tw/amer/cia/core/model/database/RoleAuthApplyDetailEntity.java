package tw.amer.cia.core.model.database;

import tw.amer.cia.core.model.database.compositeId.RoleAuthApplyDetailEntityId;
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
@Table(name = "ROLE_AUTH_APPLY_DETAIL")
@IdClass(RoleAuthApplyDetailEntityId.class)
@JsonPropertyOrder({"applyFormId", "apiId", "fabId"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class RoleAuthApplyDetailEntity implements Serializable {

    @Id
    @Column(name = "APPLY_FORM_ID", nullable = false, length = 20)
    @JsonProperty("APPLY_FORM_ID")
    private String applyFormId;

    @Id
    @Column(name = "API_ID", length = 20)
    @JsonProperty("API_ID")
    private String apiId;

    @Id
    @Column(name = "FAB_ID", nullable = false, length = 20)
    @JsonProperty("FAB_ID")
    private String fabId;

}
