package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.dao.FabSignOffConfigEntityRepo;
import tw.amer.cia.core.model.database.FabSignOffConfigEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class FabSignOffConfigEntityService {
    @Autowired
    private FabSignOffConfigEntityRepo signOffConfigEntityRepo;

    public List<FabSignOffConfigEntity> getAll() {
        return signOffConfigEntityRepo.findAll();
    }

    public Optional<FabSignOffConfigEntity> getById(String fabId) {
        return signOffConfigEntityRepo.findById(fabId);
    }

    public FabSignOffConfigEntity create(FabSignOffConfigEntity fabSystem) {
        return signOffConfigEntityRepo.save(fabSystem);
    }

    public FabSignOffConfigEntity update(String fabId, FabSignOffConfigEntity entity) throws DataSourceAccessException {
        if (StringUtils.equals(fabId, entity.getFabId())) {
            return signOffConfigEntityRepo.findById(fabId).map(existingFabSys ->
            {
                BeanUtils.copyNonNullProperties(entity, existingFabSys);
                return signOffConfigEntityRepo.save(existingFabSys);

            }).orElseThrow(() -> new EntityNotFoundException("FabSys not found with id " + fabId));
        } else {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }

    public void delete(String fabId) {
        signOffConfigEntityRepo.deleteById(fabId);
    }
}