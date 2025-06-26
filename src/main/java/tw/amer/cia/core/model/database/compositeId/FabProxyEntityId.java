package tw.amer.cia.core.model.database.compositeId;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FabProxyEntityId implements Serializable
{
    private String fabId;
    private String proxyId;
}
