package tw.amer.cia.core.model.database.compositeId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GwApikeyPermissionEntityId implements Serializable
{

    private String apikeyId;
    private String apiId;
    private String fabId;
}
