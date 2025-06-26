package tw.amer.cia.core.model.database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import lombok.experimental.FieldNameConstants;
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
@Table(name = "ROLE_DEVICE")
@JsonPropertyOrder({"deviceId", "roleId", "deviceName", "deviceIp", "devicePosition", "deviceDesc","isActive","lmUser","lmTime"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class RoleDeviceEntity implements Serializable
{

    @Id
    @Column(name = "DEVICE_ID", nullable = false, length = 20)
    @JsonProperty("DEVICE_ID")
    private String deviceId;

    @Column(name = "ROLE_ID", nullable = false, length = 20)
    @JsonProperty("ROLE_ID")
    private String roleId;

    @Column(name = "FAB_ID", nullable = false, length = 40)
    @JsonProperty("FAB_ID")
    private String fabId;

    @Column(name = "DEVICE_NAME", length = 40)
    @JsonProperty("DEVICE_NAME")
    private String deviceName;

    @Column(name = "DEVICE_IP", nullable = false, length = 40)
    @JsonProperty("DEVICE_IP")
    private String deviceIp;

    @Column(name = "DEVICE_DESC", length = 240)
    @JsonProperty("DEVICE_DESC")
    private String deviceDesc;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    @JsonProperty("IS_ACTIVE")
    private String isActive;

    @Column(name = "LM_USER", length = 40)
    @JsonProperty("LM_USER")
    @LastModifiedBy
    private String lmUser;

    @Column(name = "LM_TIME", columnDefinition = "TIMESTAMP(6)")
    @JsonProperty("LM_TIME")
    @LastModifiedDate
    private Instant lmTime;

}
