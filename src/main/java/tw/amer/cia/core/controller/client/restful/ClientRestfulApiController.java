package tw.amer.cia.core.controller.client.restful;

import tw.amer.cia.core.component.structural.annotation.ClientRestController;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.exception.gateway.GatewayControllerException;
import tw.amer.cia.core.model.database.ApiEntity;
import tw.amer.cia.core.model.database.ApiDpyEntity;
import tw.amer.cia.core.model.database.ApiGwPluginDpyEntity;
import tw.amer.cia.core.model.pojo.service.common.api.CreateOrUpdateApiEndpointDto;
import tw.amer.cia.core.service.client.ApiEntityServiceForClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@ClientRestController
@RequestMapping("/${coriander-ingress-api.client.display-name}")
public class ClientRestfulApiController
{

    @Autowired
    ApiEntityServiceForClient apiEntityServiceForClient;

    @PostMapping("/restful/admin/api")
    public void createOrUpdateApi(@Valid @RequestBody ApiEntity inDto)
    {
        // Client 提供對單一微服務的更新、Gateway操作
        apiEntityServiceForClient.createOrUpdateApi(inDto);
    }

    @DeleteMapping("/restful/admin/broadcast/api/{apiId}")
    public void tryDeleteApiBroadcastFromHost(@PathVariable String apiId)
    {
        // Client 提供對單一微服務的更新、Gateway操作
        apiEntityServiceForClient.tryDeleteApiBroadcastFromHost(apiId);
    }

    @PostMapping("/restful/admin/broadcast/api/plugin")
    public void tryCreateOrUpdateApiPluginBroadcastFromHost(@Valid @RequestBody ApiGwPluginDpyEntity inDto) throws GatewayControllerException {
        // Client 提供對單一微服務的更新、Gateway操作
        apiEntityServiceForClient.tryCreateOrUpdateApiPluginBroadcastFromHost(inDto);
    }

    @DeleteMapping("/restful/admin/api/plugin/{apiId}/{fabId}/{pluginId}")
    public ResponseEntity<Object> undeployGwPlugin(@PathVariable String apiId, @PathVariable String fabId, @PathVariable String pluginId) throws GatewayControllerException
    {
        return ResponseEntity.ok(apiEntityServiceForClient.undeployGwPlugin(apiId, fabId, pluginId));
    }

    @PostMapping("/restful/admin/api/endpoint")
    public void createOrUpdateApiEndpoint(@Valid @RequestBody CreateOrUpdateApiEndpointDto inDto) throws GatewayControllerException, DataSourceAccessException
    {
        // Client 提供對單一微服務的Endpoint更新操作，包含已在API Gateway啟動的微服務亦同步更新。
        apiEntityServiceForClient.createOrUpdateApiEndpoint(inDto);
    }

    @PostMapping("/restful/admin/api/deployment")
    public void createOrUpdateApiDeployment(@Valid @RequestBody ApiDpyEntity inDto) throws GatewayControllerException, DataSourceAccessException
    {
        // Client 提供對單一微服務的Endpoint更新操作，包含已在API Gateway啟動的微服務亦同步更新。
        apiEntityServiceForClient.createOrUpdateApiDeployment(inDto);
    }

    @DeleteMapping("/restful/admin/api/deployment")
    public void deleteApiDeployment(@Valid @RequestBody ApiDpyEntity inDto) throws GatewayControllerException
    {
        // Client 提供對單一微服務部署的刪除操作。
        apiEntityServiceForClient.deleteApiDeployment(inDto);
    }
}
