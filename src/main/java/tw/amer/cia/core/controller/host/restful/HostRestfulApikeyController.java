package tw.amer.cia.core.controller.host.restful;

import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.model.database.GwApikeyEntity;
import tw.amer.cia.core.model.pojo.service.common.apikey.UpdateApikeyPermissionFromClientCompleteDto;
import tw.amer.cia.core.service.host.ApikeyServiceForHost;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Data
@HostRestController
@RequestMapping("/${coriander-ingress-api.host.display-name}")
public class HostRestfulApikeyController
{
    @Autowired
    ApikeyServiceForHost apikeyServiceForHost;

    @PostMapping("/restful/admin/apikey")
    public void createOrUpdateApiKeyFromClient(@RequestHeader("${coriander-ingress-api.setting.identify-header}") String identifier,
                                               @RequestBody GwApikeyEntity cKeyApikey) throws DataSourceAccessException, CiaProcessorException
    {
        // Client 提供對單一Apikey的Patch up操作。
        apikeyServiceForHost.createOrUpdateApiKeyFromClient(identifier, cKeyApikey);
    }

    @PutMapping("/restful/admin/apikey/permission/{apikeyId}")
    public void updateApiKeyFromClient(@RequestHeader("${coriander-ingress-api.setting.identify-header}") String identifier,
                                       @PathVariable String apikeyId,
                                       @RequestBody UpdateApikeyPermissionFromClientCompleteDto permissionDto) throws DataSourceAccessException, CiaProcessorException
    {
        // Client 提供對單一Apikey的Patch up操作。
        apikeyServiceForHost.updateApikeyPermissionBatchFromClient(identifier, apikeyId, permissionDto);
    }

    @DeleteMapping("/restful/admin/apikey/{apikeyId}")
    public void deleteApikeyCheckFromClient(@PathVariable String apikeyId) throws DataSourceAccessException
    {
        // Client 提供對單一Apikey的Permission刪除操作。
        apikeyServiceForHost.deleteApikeyCheckFromClient(apikeyId);
    }
}
