package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.GwApikeyEntity;
import tw.amer.cia.core.model.database.GwApikeyPermissionEntity;
import tw.amer.cia.core.model.database.dao.GwApikeyEntityRepo;
import tw.amer.cia.core.model.database.dao.GwApikeyPermissionRepo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class GwApikeyEntityService
{
    @Autowired
    GwApikeyEntityRepo gwApikeyEntityRepo;

    @Autowired
    GwApikeyPermissionRepo gwApikeyPermissionRepo;

    public List<GwApikeyEntity> getAllApikeys()
    {
        return gwApikeyEntityRepo.findAll();
    }

    public Optional<GwApikeyEntity> getApikeyById(String apikeyId)
    {
        return gwApikeyEntityRepo.findByApikeyId(apikeyId);
    }

    public GwApikeyEntity createApikey(GwApikeyEntity apikey)
    {
        return gwApikeyEntityRepo.save(apikey);
    }

    public GwApikeyEntity updateApiKey(String apikeyId, GwApikeyEntity apiKeyUpdate) throws DataSourceAccessException
    {
        if (StringUtils.equals(apikeyId, apiKeyUpdate.getApikeyId()))
        {
            return gwApikeyEntityRepo.findById(apikeyId).map(existingApiKey ->
            {
                // Use null-safe checks to update properties, ignore auditing fields
                // Note: LM_USER, LM_TIME, and CREATE_TIME are not updated as they are managed by auditing
                // Check if ROLE_ID is not null and update
                if (StringUtils.isNotBlank(apiKeyUpdate.getRoleId()))
                {
                    existingApiKey.setRoleId(apiKeyUpdate.getRoleId());
                }

                // Check if KEY_NAME is not null and update
                if (StringUtils.isNotBlank(apiKeyUpdate.getKeyName()))
                {
                    existingApiKey.setKeyName(apiKeyUpdate.getKeyName());
                }

                // Check if KEY_DESC is not null and update
                if (StringUtils.isNotBlank(apiKeyUpdate.getKeyDesc()))
                {
                    existingApiKey.setKeyDesc(apiKeyUpdate.getKeyDesc());
                }

                return gwApikeyEntityRepo.save(existingApiKey);

            }).orElseThrow(() -> new EntityNotFoundException("APIKEY not found with id " + apikeyId));
        } else
        {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }

    public void deleteApikey(String apikeyId)
    {
        // Check Usages
        List<GwApikeyPermissionEntity> onSearchPermissions = gwApikeyPermissionRepo.findByApikeyId(apikeyId);
        if (CollectionUtils.isEmpty(onSearchPermissions))
        {
            gwApikeyEntityRepo.deleteById(apikeyId);
        }
    }
}
