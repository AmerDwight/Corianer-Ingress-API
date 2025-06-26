package tw.amer.cia.core.service.client.subProcess;

import tw.amer.cia.core.component.functional.gateway.GatewayControlHelper;
import tw.amer.cia.core.component.structural.annotation.ClientService;
import tw.amer.cia.core.exception.gateway.GatewayControllerException;
import tw.amer.cia.core.model.database.GwRouteEntity;
import tw.amer.cia.core.model.database.dao.ApiEndpointEntityRepo;
import tw.amer.cia.core.model.database.dao.GwRouteEntityRepo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ClientService
public class ApiServiceForClientSubProcess
{
    @Autowired
    ApiEndpointEntityRepo apiEndpointEntityRepo;
    @Autowired
    GwRouteEntityRepo gwRouteEntityRepo;
    @Autowired
    GatewayControlHelper gatewayControlHelper;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public boolean purgeApiEndpoint(String apiId) throws GatewayControllerException
    {
        boolean procedureSuccess = true;
        List<GwRouteEntity> existsGwList = gwRouteEntityRepo.findByApiId(apiId);
        if (CollectionUtils.isNotEmpty(existsGwList))
        {
            for (GwRouteEntity gwRoute : existsGwList)
            {
                // Call GW Service
                procedureSuccess = gatewayControlHelper.deleteGwRoute(gwRoute.getFabId(), gwRoute.getGwRouteId());
            }
            gwRouteEntityRepo.deleteByApiId(apiId);
        }
        long deletedData = apiEndpointEntityRepo.deleteByApiId(apiId);
        procedureSuccess &= (deletedData >= 0);

        return procedureSuccess;
    }
}
