package tw.amer.cia.core.model.database;

import tw.amer.cia.core.model.database.compositeId.SystemSignOffConfigEntityId;
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
@Table(name = "SYSTEM_SIGN_OFF_CONFIG")
@IdClass(SystemSignOffConfigEntityId.class)
@JsonPropertyOrder({"applyFormId", "roleId", "formStatus", "applicant", "createTime"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class SystemSignOffConfigEntity implements Serializable
{

    @Id
    @Column(name = "SYSTEM_ID", nullable = false, length = 20)
    @JsonProperty("SYSTEM_ID")
    private String systemId;

    @Id
    @Column(name = "USER_ID", nullable = false, length = 20)
    @JsonProperty("USER_ID")
    private String userId;

    @Column(name = "SIGNOFF_RANK")
    @JsonProperty("SIGNOFF_RANK")
    private Integer signOffRank;

}
