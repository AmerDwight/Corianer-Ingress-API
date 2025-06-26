package tw.amer.cia.core.model.pojo.component.property;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ClientPropertyFormat implements Serializable {
    private String displayName;
    private List<ClientDeployPropertyFormat> deploy;
}
