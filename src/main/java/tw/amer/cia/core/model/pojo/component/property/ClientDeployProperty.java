package tw.amer.cia.core.model.pojo.component.property;

import lombok.Data;

import java.util.List;


@Data
public class ClientDeployProperty
{
    private String identify;
    private String clientDns;
    private int clientPort;
    private List<String> adminKey;
}
