package tw.amer.cia.core.model.pojo.service.common.apikey;

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
public class UpdateApikeyPermissionFromClientCompleteDto implements Serializable
{
    private List<GwApikeyPermissionEntity> revokePermissionList;
    private List<GwApikeyPermissionEntity> grantPermissionList;

}
