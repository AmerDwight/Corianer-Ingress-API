package tw.amer.cia.core.model.pojo.component.property;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeStatusDTO implements Serializable {
    @JsonProperty("CLIENT_DNS")
    private String clientDns;
    @JsonProperty("CLIENT_PORT")
    private String clientPort;
    @JsonProperty("CLIENT_IS_ALIVE")
    @Builder.Default
    private boolean isAlive = false;
    @JsonProperty("CLIENT_INFORMATION")
    private VersionSignature versionInfo;

    public NodeStatusDTO(String clientDns, String clientPort) {

        this.clientDns = clientDns;
        this.clientPort = clientPort;
        this.isAlive = false;
        this.versionInfo = null;
    }
}
