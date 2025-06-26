package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.GwPluginEntity;
import tw.amer.cia.core.model.database.dao.GwPluginEntityRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class GwPluginEntityService
{
    @Autowired
    GwPluginEntityRepo gwPluginEntityRepo;

    public List<GwPluginEntity> getAllPlugins()
    {
        return gwPluginEntityRepo.findAll();
    }

    public Optional<GwPluginEntity> getGwPluginById(String pluginId)
    {
        return gwPluginEntityRepo.findById(pluginId);
    }

    public GwPluginEntity createGwPlugin(GwPluginEntity newPlugin)
    {
        return gwPluginEntityRepo.save(newPlugin);
    }

    public GwPluginEntity updateGwPlugin(String pluginId, GwPluginEntity pluginUpdateData) throws DataSourceAccessException
    {
        if (StringUtils.equals(pluginId, pluginUpdateData.getGwPluginId()))
        {
            Optional<GwPluginEntity> onSearchPlugin = gwPluginEntityRepo.findById(pluginId);
            if (onSearchPlugin.isPresent())
            {
                GwPluginEntity onUpdatePlugin = onSearchPlugin.get();
                BeanUtils.copyNonNullProperties(pluginUpdateData, onUpdatePlugin);
                return gwPluginEntityRepo.save(onUpdatePlugin);
            } else
            {
                throw new EntityNotFoundException("SYS_SYSTEM not found with id " + pluginId);
            }
        } else
        {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }

    public boolean createOrUpdateGwPlugin(GwPluginEntity pluginUpdateData)
    {
        boolean procedureSuccess = true;
        Optional<GwPluginEntity> inSearchPlugin = gwPluginEntityRepo.findById(pluginUpdateData.getGwPluginId());
        GwPluginEntity localPluginObject;
        if (inSearchPlugin.isPresent())
        {
            localPluginObject = inSearchPlugin.get();
            BeanUtils.copyNonNullProperties(pluginUpdateData, localPluginObject);
            gwPluginEntityRepo.save(localPluginObject);
        } else
        {
            gwPluginEntityRepo.save(pluginUpdateData);
        }
        return procedureSuccess;
    }

    public void deleteGwPlugin(String pluginId)
    {
        gwPluginEntityRepo.deleteById(pluginId);
    }
}
