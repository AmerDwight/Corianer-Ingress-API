package tw.amer.cia.core.service.database;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.GwRouteEntity;
import tw.amer.cia.core.model.database.dao.GwRouteEntityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class GwRouteEntityService
{
    @Autowired
    GwRouteEntityRepo gwRouteEntityRepo;

    public List<GwRouteEntity> getAllRoutes()
    {
        return gwRouteEntityRepo.findAll();
    }

    public Optional<GwRouteEntity> getRouteById(String gwRouteId)
    {
        return gwRouteEntityRepo.findByGwRouteId(gwRouteId);
    }

    public GwRouteEntity createRoute(GwRouteEntity route)
    {
        return gwRouteEntityRepo.save(route);
    }

    public GwRouteEntity updateRoute(String gwRouteId, GwRouteEntity gwRouteUpdate) throws DataSourceAccessException
    {
        if (gwRouteId.equals(gwRouteUpdate.getGwRouteId()))
        {
            return gwRouteEntityRepo.findById(gwRouteId).map(existingGwRoute ->
            {
                // Use null-safe checks to update properties, ignore auditing fields
                if (gwRouteUpdate.getFabId() != null)
                {
                    existingGwRoute.setFabId(gwRouteUpdate.getFabId());
                }
                if (gwRouteUpdate.getEndpointId() != null)
                {
                    existingGwRoute.setEndpointId(gwRouteUpdate.getEndpointId());
                }
                // Note: LM_USER and LM_TIME are not updated as they are managed by auditing

                return gwRouteEntityRepo.save(existingGwRoute);

            }).orElseThrow(() -> new EntityNotFoundException("GW_ROUTE not found with id " + gwRouteId));
        } else
        {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.API_RESTFUL_DATABASE_UPDATE_INPUT_ID_CONFLICT.getCompleteMessage());
        }
    }

    public void deleteRoute(String gwRouteId)
    {
        gwRouteEntityRepo.deleteById(gwRouteId);
    }
}
