package tw.amer.cia.core.service.database;


import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.ApiDpyEntity;
import tw.amer.cia.core.model.database.dao.ApiDpyEntityRepo;
import tw.amer.cia.core.model.database.compositeId.ApiDpyEntityId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class ApiDpyEntityService
{
    @Autowired
    ApiDpyEntityRepo apiDpyEntityRepo;

    public List<ApiDpyEntity> getAllFabApi()
    {
        return apiDpyEntityRepo.findAll();
    }

    public Optional<ApiDpyEntity> getFabApiById(ApiDpyEntityId id)
    {
        return apiDpyEntityRepo.findById(id);
    }

    public ApiDpyEntity createFabApi(ApiDpyEntity fabApi)
    {
        return apiDpyEntityRepo.save(fabApi);
    }

    @Transactional
    public ApiDpyEntity updateFabApi(String apiId, String fabId, ApiDpyEntity fabApi) throws DataSourceAccessException
    {
        ApiDpyEntityId fabapiId = new ApiDpyEntityId(apiId, fabId);

        if (StringUtils.equals(apiId, fabApi.getApiId()) && StringUtils.equals(fabId, fabApi.getFabId()))
        {
            return apiDpyEntityRepo.findById(fabapiId).map(existingFabapi ->
            {
                // 檢查傳入的 activeStatus 是否為 null，若不為 null 則進行更新
                if (fabApi.getActiveStatus() != null)
                {
                    existingFabapi.setActiveStatus(fabApi.getActiveStatus());
                }
                // 進行實體保存操作並返回更新後的實體
                return apiDpyEntityRepo.save(existingFabapi);
            }).orElseThrow(() -> new EntityNotFoundException("Fabapi not found with id " + fabapiId));
        } else
        {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }

    }

    public void deleteFabApi(ApiDpyEntityId id)
    {
        apiDpyEntityRepo.deleteById(id);
    }
}