package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.SystemDpyEntity;
import tw.amer.cia.core.model.database.dao.SystemDpyEntityRepo;
import tw.amer.cia.core.model.database.compositeId.SystemDpyEntityId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class SystemDpyEntityService
{
    @Autowired
    private SystemDpyEntityRepo systemDpyEntityRepo;

    public List<SystemDpyEntity> getAllFabSystems()
    {
        return systemDpyEntityRepo.findAll();
    }

    public Optional<SystemDpyEntity> getFabSystemById(SystemDpyEntityId cFabSysId)
    {
        return systemDpyEntityRepo.findById(cFabSysId);
    }

    public SystemDpyEntity createFabSystem(SystemDpyEntity fabSystem)
    {
        return systemDpyEntityRepo.save(fabSystem);
    }

    public SystemDpyEntity updateFabSys(String systemId, String fabId, SystemDpyEntity fabSysUpdate) throws DataSourceAccessException
    {
        SystemDpyEntityId id = new SystemDpyEntityId(systemId, fabId);
        if (StringUtils.equals(id.getSystemId(), fabSysUpdate.getSystemId()) &&
                StringUtils.equals(id.getFabId(), fabSysUpdate.getFabId()))
        {
            return systemDpyEntityRepo.findById(id).map(existingFabSys ->
            {
                // 使用 null-safe 檢查來更新屬性，忽略LM_USER和LM_TIME
                if (fabSysUpdate.getSystemHost() != null)
                {
                    existingFabSys.setSystemHost(fabSysUpdate.getSystemHost());
                }
                if (fabSysUpdate.getSystemPort() != null)
                {
                    existingFabSys.setSystemPort(fabSysUpdate.getSystemPort());
                }
                if (fabSysUpdate.getScheme() != null)
                {
                    existingFabSys.setScheme(fabSysUpdate.getScheme());
                }
                if (fabSysUpdate.getHealthCheckPath() != null)
                {
                    existingFabSys.setHealthCheckPath(fabSysUpdate.getHealthCheckPath());
                }
                if (fabSysUpdate.getActiveStatus() != null)
                {
                    existingFabSys.setActiveStatus(fabSysUpdate.getActiveStatus());
                }

                return systemDpyEntityRepo.save(existingFabSys);

            }).orElseThrow(() -> new EntityNotFoundException("FabSys not found with id " + id));
        } else
        {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }

    public void deleteFabSystem(SystemDpyEntityId cFabSysId)
    {
        systemDpyEntityRepo.deleteById(cFabSysId);
    }
}