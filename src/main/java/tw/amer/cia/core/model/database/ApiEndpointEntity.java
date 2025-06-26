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
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldNameConstants
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "API_ENDPOINT")
@JsonPropertyOrder({"endpointId", "apiId", "apiItfType", "apiHostUri", "apiGwUri", "httpMethod", "activeStatus", "lmUser", "lmTime"})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class ApiEndpointEntity implements Serializable {

    @Id
    @Column(name = "ENDPOINT_ID", length = 20)
    @JsonProperty("ENDPOINT_ID")
    private String endpointId;

    @Column(name = "API_ID", length = 20)
    @JsonProperty("API_ID")
    private String apiId;

    @Column(name = "API_ITF_TYPE", length = 20)
    @JsonProperty("API_ITF_TYPE")
    private String apiItfType;

    @Column(name = "API_HOST_URI", length = 100)
    @JsonProperty("API_HOST_URI")
    private String apiHostUri;

    @Column(name = "API_GW_URI", length = 100)
    @JsonProperty("API_GW_URI")
    private String apiGwUri;

    @Column(name = "HTTP_METHOD", length = 100)
    @JsonProperty("HTTP_METHOD")
    private String httpMethod;

    @Column(name = "ACTIVE_STATUS", length = 20)
    @JsonProperty("ACTIVE_STATUS")
    private String activeStatus;

    @Column(name = "LM_USER", length = 40)
    @JsonProperty("LM_USER")
    @LastModifiedBy
    private String lmUser;

    @Column(name = "LM_TIME", columnDefinition = "TIMESTAMP(6)")
    @JsonProperty("LM_TIME")
    @LastModifiedDate
    private Instant lmTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiEndpointEntity that = (ApiEndpointEntity) o;

        if (!Objects.equals(endpointId, that.endpointId)) return false;
        if (!Objects.equals(apiId, that.apiId)) return false;
        if (!Objects.equals(apiItfType, that.apiItfType)) return false;
        if (!Objects.equals(apiHostUri, that.apiHostUri)) return false;
        if (!Objects.equals(apiGwUri, that.apiGwUri)) return false;
        return Objects.equals(httpMethod, that.httpMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endpointId, apiId, apiItfType, apiHostUri, apiGwUri, httpMethod);
    }
}
