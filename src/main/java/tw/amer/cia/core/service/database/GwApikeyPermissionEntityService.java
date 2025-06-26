package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.GwApikeyPermissionEntity;
import tw.amer.cia.core.model.database.dao.GwApikeyPermissionRepo;
import tw.amer.cia.core.model.database.compositeId.GwApikeyPermissionEntityId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GwApikeyPermissionEntityService
{
    @Autowired
    GwApikeyPermissionRepo gwApikeyPermissionRepo;

    public List<GwApikeyPermissionEntity> getAllPermissions()
    {
        return gwApikeyPermissionRepo.findAll();
    }

    public Optional<GwApikeyPermissionEntity> getPermissionById(String apikeyId, String apiId, String fabId)
    {
        return gwApikeyPermissionRepo.findById(new GwApikeyPermissionEntityId(apikeyId, apiId, fabId));
    }

    public GwApikeyPermissionEntity createPermission(GwApikeyPermissionEntity permission)
    {
        return gwApikeyPermissionRepo.save(permission);
    }

    public GwApikeyPermissionEntity updateKeyPermission(String apikeyId, String apiId, String fabId, GwApikeyPermissionEntity keyPermissionUpdate) throws DataSourceAccessException
    {
        throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                ErrorConstantLib.SERVICE_LOGICAL_ERROR_RELATION_ENTITY_NO_UPDATE.getCompleteMessage());
    }

    public void deletePermission(String apikeyId, String apiId, String fabId)
    {
        gwApikeyPermissionRepo.deleteById(new GwApikeyPermissionEntityId(apikeyId, apiId, fabId));
    }
}
