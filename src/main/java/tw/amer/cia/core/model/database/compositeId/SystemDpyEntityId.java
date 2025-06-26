package tw.amer.cia.core.model.database.compositeId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemDpyEntityId implements Serializable
{

    private String systemId;
    private String fabId;
}
