package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.utility.BeanUtils;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.ApiEntity;
import tw.amer.cia.core.model.database.dao.ApiEntityRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class ApiEntityService {
    @Autowired
    ApiEntityRepo apiEntityRepo;

    public List<ApiEntity> getAllApi() {
        return apiEntityRepo.findAll();
    }

    public Optional<ApiEntity> getApiById(String apiId) {
        return apiEntityRepo.findByApiId(apiId);
    }

    public ApiEntity createApi(ApiEntity api) {
        return apiEntityRepo.save(api);
    }

    public ApiEntity updateApi(String apiId, ApiEntity api) throws DataSourceAccessException {
        if (StringUtils.equals(apiId, api.getApiId())) {
            return apiEntityRepo.findById(apiId).map(apiEntity ->
            {
                BeanUtils.copyNonNullProperties(api, apiEntity);
                return apiEntityRepo.save(apiEntity);

            }).orElseThrow(() -> new EntityNotFoundException("API not found with id " + apiId));
        } else {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }

    public void deleteApi(String apiId) {
        apiEntityRepo.deleteById(apiId);
    }
}
