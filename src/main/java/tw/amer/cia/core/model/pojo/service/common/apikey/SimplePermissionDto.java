package tw.amer.cia.core.model.pojo.service.common.apikey;

import tw.amer.cia.core.model.pojo.service.common.role.SimpleAuthority;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import java.io.Serializable;

@Data
@SuperBuilder
public class SimplePermissionDto extends SimpleAuthority implements Serializable
{
    public SimplePermissionDto(@Valid String sysName, @Valid String apiName)
    {
        super(sysName, apiName);
    }
}
