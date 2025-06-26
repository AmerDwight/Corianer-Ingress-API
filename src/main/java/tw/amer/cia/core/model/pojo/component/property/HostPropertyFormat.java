package tw.amer.cia.core.model.pojo.component.property;

import lombok.Data;

import java.io.Serializable;

@Data
public class HostPropertyFormat implements Serializable
{
    private String displayName;
    private String hostDns;
    private int hostPort;
    private String adminKey;
}
