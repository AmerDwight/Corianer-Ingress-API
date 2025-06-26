package tw.amer.cia.core.model.pojo.component.property;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VersionSignature implements Serializable {
    @JsonProperty("PROJECT")
    String project;
    @JsonProperty("VERSION")
    String version;
    @JsonProperty("DEPLOY_TYPE")
    String deployType;
    @JsonProperty("DEPLOY_NAME")
    String deployName;

}
