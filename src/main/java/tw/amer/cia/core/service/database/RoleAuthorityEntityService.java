package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.RoleAuthorityEntity;
import tw.amer.cia.core.model.database.dao.RoleAuthorityEntityRepo;
import tw.amer.cia.core.model.database.compositeId.RoleAuthoroityEntityId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class RoleAuthorityEntityService
{
    @Autowired
    RoleAuthorityEntityRepo roleAuthorityEntityRepo;

    public List<RoleAuthorityEntity> getAllRoleAuthorities()
    {
        return roleAuthorityEntityRepo.findAll();
    }

    public Optional<RoleAuthorityEntity> getRoleAuthorityById(RoleAuthoroityEntityId id)
    {
        return roleAuthorityEntityRepo.findById(id);
    }

    public RoleAuthorityEntity createRoleAuthority(RoleAuthorityEntity roleAuthority)
    {
        return roleAuthorityEntityRepo.save(roleAuthority);
    }

    public RoleAuthorityEntity updateRoleAuthority(String roleId, String apiId, String fabId, RoleAuthorityEntity roleAuthorityUpdate) throws DataSourceAccessException
    {
        RoleAuthoroityEntityId id = new RoleAuthoroityEntityId(roleId, apiId, fabId);
        if (StringUtils.equals(id.getRoleId(), roleAuthorityUpdate.getRoleId()) &&
                StringUtils.equals(id.getApiId(), roleAuthorityUpdate.getApiId()) &&
                StringUtils.equals(id.getFabId(), roleAuthorityUpdate.getFabId()))
        {

            return roleAuthorityEntityRepo.findById(id).map(existingRoleAuthority ->
            {
                // Use null-safe checks to update properties, ignore auditing fields
                if (roleAuthorityUpdate.getApplyFormNumber() != null)
                {
                    existingRoleAuthority.setApplyFormNumber(roleAuthorityUpdate.getApplyFormNumber());
                }
                // Note: LM_USER and LM_TIME are not updated as they are managed by auditing

                return roleAuthorityEntityRepo.save(existingRoleAuthority);

            }).orElseThrow(() -> new EntityNotFoundException("ROLE_AUTHORITY not found with id " + id));
        } else
        {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }

    public void deleteRoleAuthority(String roleId, String apiId, String fabId)
    {
        roleAuthorityEntityRepo.deleteById(new RoleAuthoroityEntityId(roleId, apiId, fabId));
    }
}
