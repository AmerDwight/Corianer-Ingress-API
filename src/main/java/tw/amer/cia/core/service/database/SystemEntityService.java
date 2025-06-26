package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.SystemEntity;
import tw.amer.cia.core.model.database.dao.SystemEntityRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class SystemEntityService
{
    @Autowired
    SystemEntityRepo systemEntityRepo;

    public List<SystemEntity> getAllSystems()
    {
        return systemEntityRepo.findAll();
    }

    public Optional<SystemEntity> getSystemById(String systemId)
    {
        return systemEntityRepo.findById(systemId);
    }

    public SystemEntity createSystem(SystemEntity system)
    {
        return systemEntityRepo.save(system);
    }

    public SystemEntity updateSystem(String systemId, SystemEntity sysSystemUpdate) throws DataSourceAccessException
    {
        if (StringUtils.equals(systemId, sysSystemUpdate.getSystemId()))
        {
            return systemEntityRepo.findById(systemId).map(existingSysSystem ->
            {
                // Use null-safe checks to update properties, ignore auditing fields
                if (sysSystemUpdate.getSystemName() != null)
                {
                    existingSysSystem.setSystemName(sysSystemUpdate.getSystemName());
                }
                if (sysSystemUpdate.getServiceLevel() != null)
                {
                    existingSysSystem.setServiceLevel(sysSystemUpdate.getServiceLevel());
                }
                if (sysSystemUpdate.getOwner() != null)
                {
                    existingSysSystem.setOwner(sysSystemUpdate.getOwner());
                }
                if (sysSystemUpdate.getActiveStatus() != null)
                {
                    existingSysSystem.setActiveStatus(sysSystemUpdate.getActiveStatus());
                }
                if (sysSystemUpdate.getApplicableFlag() != null)
                {
                    existingSysSystem.setApplicableFlag(sysSystemUpdate.getApplicableFlag());
                }
                // Note: LM_USER, LM_TIME, and CREATE_TIME are not updated as they are managed by auditing

                return systemEntityRepo.save(existingSysSystem);

            }).orElseThrow(() -> new EntityNotFoundException("SYS_SYSTEM not found with id " + systemId));
        } else
        {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }

    public void deleteSystem(String systemId)
    {
        systemEntityRepo.deleteById(systemId);
    }
}
