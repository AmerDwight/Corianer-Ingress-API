package tw.amer.cia.core.controller.host.restful;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.pojo.service.common.api.DeployApiGwPluginDto;
import tw.amer.cia.core.model.pojo.service.common.gateway.CreateOrUpdateGwPluginDto;
import tw.amer.cia.core.service.host.ApiServiceForHost;

@HostRestController
@RequestMapping("/${coriander-ingress-api.host.display-name}")
public class HostRestfulApiController {

    @Autowired
    ApiServiceForHost apiServiceForHost;

    @GetMapping("/restful/gw/plugin")
    public ResponseEntity<Object> getAllGwPluginInfo() {
        return ResponseEntity.ok(apiServiceForHost.getAllGwPluginInfo());
    }

    @PatchMapping("/restful/gw/plugin")
    public ResponseEntity<Object> createOrUpdateGwPlugin(@RequestBody CreateOrUpdateGwPluginDto newPlugin) throws CiaProcessorException {
        apiServiceForHost.createOrUpdateGwPlugin(newPlugin);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/restful/gw/plugin/{gwPluginId}")
    public ResponseEntity<Object> undeployGwPluginCimRequestVerify(@PathVariable String gwPluginId) throws CiaProcessorException {
        apiServiceForHost.deleteGwPlugin(gwPluginId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/restful/api/plugin")
    public ResponseEntity<Object> createOrUpdateMsPlugin(@RequestBody DeployApiGwPluginDto newPlugin) throws CiaProcessorException, DataSourceAccessException {
        apiServiceForHost.createOrUpdateApiPlugin(newPlugin);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/restful/api/plugin/{apiId}/{fabId}/{gwPluginId}")
    public ResponseEntity<Object> undeployMsPlugin(@PathVariable String apiId, @PathVariable String fabId, @PathVariable String gwPluginId) throws CiaProcessorException {
        apiServiceForHost.undeployApiPlugin(apiId, fabId, gwPluginId);
        return ResponseEntity.ok().build();
    }
}
