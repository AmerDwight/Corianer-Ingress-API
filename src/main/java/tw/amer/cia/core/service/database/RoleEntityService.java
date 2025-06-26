package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.RoleEntity;
import tw.amer.cia.core.model.database.dao.RoleEntityRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class RoleEntityService
{
    @Autowired
    RoleEntityRepo roleEntityRepo;

    public List<RoleEntity> getAllRoles()
    {
        return roleEntityRepo.findAll();
    }

    public Optional<RoleEntity> getRoleById(String roleId)
    {
        return roleEntityRepo.findById(roleId);
    }

    public RoleEntity createRole(RoleEntity role)
    {
        return roleEntityRepo.save(role);
    }

    public RoleEntity updateRole(String roleId, RoleEntity roleRoleUpdate) throws DataSourceAccessException
    {
        if (StringUtils.equals(roleId, roleRoleUpdate.getRoleId()))
        {
            return roleEntityRepo.findById(roleId).map(existingRoleRole ->
            {
                // Use null-safe checks to update properties, ignore auditing fields
                if (roleRoleUpdate.getRoleType() != null)
                {
                    existingRoleRole.setRoleType(roleRoleUpdate.getRoleType());
                }
                if (roleRoleUpdate.getRoleName() != null)
                {
                    existingRoleRole.setRoleName(roleRoleUpdate.getRoleName());
                }
                if (roleRoleUpdate.getRoleDesc() != null)
                {
                    existingRoleRole.setRoleDesc(roleRoleUpdate.getRoleDesc());
                }
                // Note: LM_USER and LM_TIME are not updated as they are managed by auditing

                return roleEntityRepo.save(existingRoleRole);

            }).orElseThrow(() -> new EntityNotFoundException("ROLE_ROLE not found with id " + roleId));
        } else
        {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }

    public void deleteRole(String roleId)
    {
        roleEntityRepo.deleteById(roleId);
    }

}
