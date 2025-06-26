package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.RoleUserEntity;
import tw.amer.cia.core.model.database.dao.RoleUserEntityRepo;
import tw.amer.cia.core.model.database.compositeId.RoleUserEntityId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleUserEntityService
{
    @Autowired
    RoleUserEntityRepo roleUserEntityRepo;

    public List<RoleUserEntity> getAllRoleUsers()
    {
        return roleUserEntityRepo.findAll();
    }

    public Optional<RoleUserEntity> getRoleUserById(RoleUserEntityId id)
    {
        return roleUserEntityRepo.findById(id);
    }

    public RoleUserEntity createRoleUser(RoleUserEntity roleUser)
    {
        return roleUserEntityRepo.save(roleUser);
    }

    public RoleUserEntity updateRoleUsr(String userId, String roleId, RoleUserEntity roleUsrUpdate) throws DataSourceAccessException
    {
        throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                ErrorConstantLib.SERVICE_LOGICAL_ERROR_RELATION_ENTITY_NO_UPDATE.getCompleteMessage());
    }

    public void deleteRoleUser(RoleUserEntityId id)
    {
        roleUserEntityRepo.deleteById(id);
    }
}
