package tw.amer.cia.core.model.database.compositeId;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiDpyEntityId implements Serializable
{
    private String apiId;
    private String fabId;
}
