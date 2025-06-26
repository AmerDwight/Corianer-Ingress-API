package tw.amer.cia.core.model.pojo.service.common.role;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
public class SimpleAuthorityDto extends SimpleAuthority implements Serializable
{
    public SimpleAuthorityDto(String sysName, String apiName)
    {
        super(sysName, apiName);
    }
}
