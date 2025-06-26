package tw.amer.cia.core.model.database;

import tw.amer.cia.core.model.database.compositeId.ApiGwPluginDpyEntityId;
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
@Table(name = "API_GW_PLUGIN_DPY")
@IdClass(ApiGwPluginDpyEntityId.class)
@JsonPropertyOrder({"apiId", "fabId", "gwPluginId", "gwPluginParameter"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class ApiGwPluginDpyEntity implements Serializable {

    @Id
    @Column(name = "API_ID", length = 20)
    @JsonProperty("API_ID")
    private String apiId;

    @Id
    @Column(name = "FAB_ID", length = 20)
    @JsonProperty("FAB_ID")
    private String fabId;

    @Id
    @Column(name = "GW_PLUGIN_ID", nullable = false, length = 20)
    @JsonProperty("GW_PLUGIN_ID")
    private String gwPluginId;

    @Column(name = "GW_PLUGIN_PARAMETER", length = 2000)
    @JsonProperty("GW_PLUGIN_PARAMETER")
    private String gwPluginParameter;

}
