package tw.amer.cia.core.model.pojo.component.property;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ClientDeployPropertyFormat implements Serializable
{
    List<String> fab;
    private String siteName;
    private List<ClientDeployProperty> deployList;
}
