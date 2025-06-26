package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.ApiEndpointEntity;
import tw.amer.cia.core.model.database.dao.ApiEndpointEntityRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class ApiEndpointEntityService
{
    @Autowired
    private ApiEndpointEntityRepo apiEndpointEntityRepo;

    public List<ApiEndpointEntity> getAllApiEndpoints()
    {
        return apiEndpointEntityRepo.findAll();
    }

    public Optional<ApiEndpointEntity> getApiEndpointById(String endpointId)
    {
        return apiEndpointEntityRepo.findById(endpointId);
    }

    public ApiEndpointEntity createApiEndpoint(ApiEndpointEntity apiEndpoint)
    {
        return apiEndpointEntityRepo.save(apiEndpoint);
    }

    public ApiEndpointEntity updateApiEndpoint(String endpointId, ApiEndpointEntity apiEndpointUpdate) throws DataSourceAccessException
    {
        if (StringUtils.equals(endpointId, apiEndpointUpdate.getEndpointId()))
        {
            return apiEndpointEntityRepo.findById(endpointId).map(existingEndpoint ->
            {
                // Use null-safe checks to update properties
                if (apiEndpointUpdate.getApiId() != null)
                {
                    existingEndpoint.setApiId(apiEndpointUpdate.getApiId());
                }
                if (apiEndpointUpdate.getApiItfType() != null)
                {
                    existingEndpoint.setApiItfType(apiEndpointUpdate.getApiItfType());
                }
                if (apiEndpointUpdate.getApiHostUri() != null)
                {
                    existingEndpoint.setApiHostUri(apiEndpointUpdate.getApiHostUri());
                }
                if (apiEndpointUpdate.getApiGwUri() != null)
                {
                    existingEndpoint.setApiGwUri(apiEndpointUpdate.getApiGwUri());
                }
                if (apiEndpointUpdate.getHttpMethod() != null)
                {
                    existingEndpoint.setHttpMethod(apiEndpointUpdate.getHttpMethod());
                }
                if (apiEndpointUpdate.getActiveStatus() != null)
                {
                    existingEndpoint.setActiveStatus(apiEndpointUpdate.getActiveStatus());
                }
                // Note: LM_USER and LM_TIME are not updated as they are managed by auditing

                return apiEndpointEntityRepo.save(existingEndpoint);

            }).orElseThrow(() -> new EntityNotFoundException("API_ENDPOINT not found with id " + endpointId));
        } else
        {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }

    public void deleteApiEndpoint(String endpointId)
    {
        apiEndpointEntityRepo.deleteById(endpointId);
    }

    public void deleteApiEndpointByApiId(String apiId)
    {
        apiEndpointEntityRepo.deleteByApiId(apiId);
    }
}