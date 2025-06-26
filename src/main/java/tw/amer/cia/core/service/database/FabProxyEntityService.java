package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.FabProxyEntity;
import tw.amer.cia.core.model.database.dao.FabProxyEntityRepo;
import tw.amer.cia.core.model.database.compositeId.FabProxyEntityId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class FabProxyEntityService
{
    @Autowired
    private FabProxyEntityRepo fabProxyEntityRepo;

    public List<FabProxyEntity> getAll()
    {
        return fabProxyEntityRepo.findAll();
    }

    public Optional<FabProxyEntity> getById(FabProxyEntityId cFabProxyId)
    {
        return fabProxyEntityRepo.findById(cFabProxyId);
    }

    public FabProxyEntity create(FabProxyEntity fabSystem)
    {
        return fabProxyEntityRepo.save(fabSystem);
    }

    public FabProxyEntity update(String fabId, String proxyId, FabProxyEntity entity) throws DataSourceAccessException
    {
        FabProxyEntityId id = new FabProxyEntityId(fabId, proxyId);
        if (StringUtils.equals(id.getProxyId(), entity.getProxyId()) &&
                StringUtils.equals(id.getFabId(), entity.getFabId()))
        {
            return fabProxyEntityRepo.findById(id).map(existingFabSys ->
            {
                BeanUtils.copyNonNullProperties(entity, existingFabSys);
                return fabProxyEntityRepo.save(existingFabSys);

            }).orElseThrow(() -> new EntityNotFoundException("FabSys not found with id " + id));
        } else
        {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }

    public void delete(FabProxyEntityId cFabProxyId)
    {
        fabProxyEntityRepo.deleteById(cFabProxyId);
    }
}