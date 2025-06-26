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
@Table(name = "GW_PLUGIN")
@JsonPropertyOrder({"gwPluginId", "gwPluginType", "gwPluginName", "gwPluginDeploy", "gwPluginTemplate", "gwPluginSample", "lmUser", "lmTime"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class GwPluginEntity implements Serializable {

    @Id
    @Column(name = "GW_PLUGIN_ID", nullable = false, length = 20)
    @JsonProperty("GW_PLUGIN_ID")
    private String gwPluginId;

    @Column(name = "GW_PLUGIN_TYPE", length = 40)
    @JsonProperty("GW_PLUGIN_TYPE")
    private String gwPluginType;

    @Column(name = "GW_PLUGIN_NAME", length = 40)
    @JsonProperty("GW_PLUGIN_NAME")
    private String gwPluginName;

    @Column(name = "GW_PLUGIN_DESC", length = 240)
    @JsonProperty("GW_PLUGIN_DESC")
    private String gwPluginDesc;

    @Column(name = "GW_PLUGIN_DEPLOY", length = 40)
    @JsonProperty("GW_PLUGIN_DEPLOY")
    private String gwPluginDeploy;

    @Column(name = "GW_PLUGIN_TEMPLATE", length = 100)
    @JsonProperty("GW_PLUGIN_TEMPLATE")
    private String gwPluginTemplate;

    @Column(name = "GW_PLUGIN_SAMPLE", length = 2000)
    @JsonProperty("GW_PLUGIN_SAMPLE")
    private String gwPluginSample;

    @Column(name = "LM_USER", length = 40)
    @JsonProperty("LM_USER")
    @LastModifiedBy
    private String lmUser;

    @Column(name = "LM_TIME", columnDefinition = "TIMESTAMP(6)")
    @JsonProperty("LM_TIME")
    @LastModifiedDate
    private Instant lmTime;


}
