package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.ExternalSystemConfigEntity;
import tw.amer.cia.core.model.database.dao.ExternalSystemConfigEntityRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ExternalSystemConfigEntityService {
    @Autowired
    private ExternalSystemConfigEntityRepo externalSystemConfigEntityRepo;

    public List<ExternalSystemConfigEntity> getAll() {
        return externalSystemConfigEntityRepo.findAll();
    }

    public Optional<ExternalSystemConfigEntity> getById(String idIndex) {
        return externalSystemConfigEntityRepo.findById(idIndex);
    }

    public ExternalSystemConfigEntity create(ExternalSystemConfigEntity entity) {
        return externalSystemConfigEntityRepo.save(entity);
    }

    public void create(Collection<ExternalSystemConfigEntity> entities) throws DataSourceAccessException {
        for (ExternalSystemConfigEntity entity : entities) {
            checkEntityContent(entity);
        }
        externalSystemConfigEntityRepo.saveAll(entities);
    }

    public ExternalSystemConfigEntity update(String extEntityId, ExternalSystemConfigEntity entity) throws DataSourceAccessException {

        if (StringUtils.equals(extEntityId, entity.getExtSystemId())) {
            return externalSystemConfigEntityRepo.findById(extEntityId).map(object ->
            {
                BeanUtils.copyNonNullProperties(entity, object);
                return externalSystemConfigEntityRepo.save(object);

            }).orElseThrow(() -> new EntityNotFoundException("Hostname not found with id " + extEntityId));
        } else {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }

    public ExternalSystemConfigEntity createOrUpdateExtEntity(ExternalSystemConfigEntity entity) throws DataSourceAccessException {
        this.checkEntityContent(entity);
        ExternalSystemConfigEntity onUpdateObj;

        Optional<ExternalSystemConfigEntity> onSearchExists = externalSystemConfigEntityRepo.findById(entity.getExtSystemId());
        if (onSearchExists.isPresent()) {
            onUpdateObj = onSearchExists.get();
        } else {
            onUpdateObj = new ExternalSystemConfigEntity();
        }
        BeanUtils.copyNonNullProperties(entity, onUpdateObj);
        externalSystemConfigEntityRepo.save(onUpdateObj);
        return onUpdateObj;
    }

    public void delete(String hostname) {
        externalSystemConfigEntityRepo.deleteById(hostname);
    }

    public boolean checkEntityContent(ExternalSystemConfigEntity entity) throws DataSourceAccessException {
        if (entity == null) {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.GENERAL_API_MISSING_CRITICAL_DATA_INPUT.getCompleteMessage() +
                            " Entity is null. "
            );
        }
        if (StringUtils.isBlank(entity.getExtSystemId()) || StringUtils.isBlank(entity.getExtSystemKey())) {
            throw DataSourceAccessException.createExceptionForHttp(
                    HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.GENERAL_API_MISSING_CRITICAL_DATA_INPUT.getCompleteMessage() +
                            " ExternalSystemConfigEntity Id or key is missing.");
        }
        return true;
    }
}