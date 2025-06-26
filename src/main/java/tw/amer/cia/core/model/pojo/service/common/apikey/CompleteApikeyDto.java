package tw.amer.cia.core.model.pojo.service.common.apikey;

import tw.amer.cia.core.model.database.GwApikeyEntity;
import tw.amer.cia.core.model.database.GwApikeyPermissionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteApikeyDto implements Serializable
{

    private GwApikeyEntity apikey;
    private List<GwApikeyPermissionEntity> permissionList;

}
