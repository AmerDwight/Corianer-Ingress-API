package tw.amer.cia.core.component.functional.coriander;

import tw.amer.cia.core.model.database.*;
import tw.amer.cia.core.model.pojo.service.common.AllProxyDataDto;
import tw.amer.cia.core.model.pojo.service.common.apikey.CompleteApikeyDto;
import tw.amer.cia.core.model.pojo.service.common.api.CompleteApiDto;
import tw.amer.cia.core.model.pojo.service.common.role.CompleteRoleDto;
import tw.amer.cia.core.model.pojo.service.common.system.CompleteSystemDto;

import java.util.Collection;
import java.util.List;

public interface CallHostApiComponent
{
    boolean checkHostAlive();

    boolean createOrUpdateApikeyFromClient(GwApikeyEntity newApikey);

    List<FabEntity> obtainFabDataByFabCollection(Collection<String> fabCollection);

    List<CompleteSystemDto> obtainCompleteSystemDataByFabCollection(Collection<String> fabCollection);

    List<GwPluginEntity> obtainAllGwPluginData();

    List<CompleteApiDto> obtainCompleteApiDataByFabCollection(Collection<String> fabCollection);

    List<CompleteRoleDto> obtainCompleteRoleDataByFabCollection(Collection<String> fabCollection);
    List<RoleDeviceEntity> obtainRoleDeviceDataByFabCollection(Collection<String> fabCollection);

    List<CompleteApikeyDto> obtainCompleteApikeyDataByFabCollection(Collection<String> fabCollection);

    AllProxyDataDto obtainAllProxyData();

    GwApikeyEntity obtainApikeyByApikeyId(String apikeyId);

    boolean updateApikeyPermissionBatchFromClient(String apikeyId, List<GwApikeyPermissionEntity> revokePermissionList, List<GwApikeyPermissionEntity> grantPermissionList);

    boolean deleteApikeyCheckFromClient(String apikeyId);

    List<ExternalSystemConfigEntity> obtainAllExtCtlEntity();
}
