package tw.amer.cia.core.controller.client.restful;

import tw.amer.cia.core.component.structural.annotation.ClientRestController;
import tw.amer.cia.core.exception.gateway.ApisixProcessorException;
import tw.amer.cia.core.exception.gateway.GatewayControllerException;
import tw.amer.cia.core.model.database.GwApikeyEntity;
import tw.amer.cia.core.model.database.GwApikeyPermissionEntity;
import tw.amer.cia.core.service.client.ApikeyServiceForClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@ClientRestController
@RequestMapping("/${coriander-ingress-api.client.display-name}")
public class ClientRestfulApikeyController
{

    @Autowired
    ApikeyServiceForClient apikeyServiceForClient;

    @PostMapping("/restful/admin/apikey")
    public void createOrUpdateApikeyFromHost(@Valid @RequestBody GwApikeyEntity inDto) throws GatewayControllerException
    {
        apikeyServiceForClient.createOrUpdateApikeyBySiteFromHost(inDto);
    }

    @PostMapping("/restful/admin/apikey/permission")
    public void createOrUpdateApikeyPermissionFromHostBatch(@Valid @RequestBody List<GwApikeyPermissionEntity> inDto) throws GatewayControllerException
    {
        // Client 提供對單一Apikey的Permission 更新/刪除操作。
        apikeyServiceForClient.createOrUpdateApikeyPermissionFromHostBatch(inDto);
    }

    @DeleteMapping("/restful/admin/apikey/permission")
    public void revokeApikeyPermissionFromHostBatch(@Valid @RequestBody List<GwApikeyPermissionEntity> inDto) throws GatewayControllerException
    {
        // Client 提供對單一Apikey的Permission刪除操作。
        apikeyServiceForClient.revokeApikeyPermissionFromHostBatch(inDto);
    }

    @PutMapping("/restful/admin/broadcast/apikey/active/change/{apikeyId}")
    public void tryUpdateApikeyActiveStatusBroadcastNoReplyFromHost(@PathVariable String apikeyId) throws ApisixProcessorException {
        apikeyServiceForClient.tryUpdateApikeyActiveStatusBroadcastNoReplyFromHost(apikeyId);
    }
}
