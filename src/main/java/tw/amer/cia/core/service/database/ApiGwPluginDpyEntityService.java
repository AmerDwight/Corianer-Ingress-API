package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.ApiGwPluginDpyEntity;
import tw.amer.cia.core.model.database.dao.ApiGwPluginDpyEntityRepo;
import tw.amer.cia.core.model.database.compositeId.ApiGwPluginDpyEntityId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class ApiGwPluginDpyEntityService
{
    @Autowired
    private ApiGwPluginDpyEntityRepo apiGwPluginDpyEntityRepo;

    public List<ApiGwPluginDpyEntity> getAll()
    {
        return apiGwPluginDpyEntityRepo.findAll();
    }

    public Optional<ApiGwPluginDpyEntity> getById(ApiGwPluginDpyEntityId idIndex)
    {
        return apiGwPluginDpyEntityRepo.findById(idIndex);
    }

    public ApiGwPluginDpyEntity create(ApiGwPluginDpyEntity entity)
    {
        return apiGwPluginDpyEntityRepo.save(entity);
    }

    public ApiGwPluginDpyEntity update(ApiGwPluginDpyEntityId targetId, ApiGwPluginDpyEntity entity) throws DataSourceAccessException
    {

        ApiGwPluginDpyEntityId entityId = ApiGwPluginDpyEntityId.builder()
                .apiId(entity.getApiId())
                .fabId(entity.getFabId())
                .gwPluginId(entity.getGwPluginId())
                .build();
        if (targetId.equals(entityId))
        {
            return apiGwPluginDpyEntityRepo.findById(targetId).map(object ->
            {
                BeanUtils.copyNonNullProperties(entity, object);
                return apiGwPluginDpyEntityRepo.save(object);

            }).orElseThrow(() -> new EntityNotFoundException("FabSys not found with id " + targetId));
        } else
        {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }

    public void delete(ApiGwPluginDpyEntityId targetId)
    {
        apiGwPluginDpyEntityRepo.deleteById(targetId);
    }
}