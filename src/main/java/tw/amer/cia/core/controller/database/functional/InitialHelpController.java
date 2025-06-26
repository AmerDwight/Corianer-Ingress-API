package tw.amer.cia.core.controller.database.functional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.model.database.RoleDeviceEntity;
import tw.amer.cia.core.model.pojo.service.common.AllProxyDataDto;
import tw.amer.cia.core.model.pojo.service.common.apikey.CompleteApikeyDto;
import tw.amer.cia.core.model.pojo.service.common.api.CompleteApiDto;
import tw.amer.cia.core.model.pojo.service.common.role.CompleteRoleDto;
import tw.amer.cia.core.model.pojo.service.common.system.CompleteSystemDto;
import tw.amer.cia.core.service.host.*;

import java.util.List;

@HostRestController
@RequestMapping("/database/initial")
public class InitialHelpController
{

    @Autowired
    SystemServiceForHost systemServiceForHost;
    @Autowired
    ApiServiceForHost apiServiceForHost;
    @Autowired
    RoleServiceForHost roleServiceForHost;

    @Autowired
    RoleDeviceServiceForHost roleDeviceServiceForHost;
    @Autowired
    ApikeyServiceForHost apikeyServiceForHost;
    @Autowired
    ProxyServiceForHost proxyServiceForHost;

    @GetMapping("/system/criteria")
    public ResponseEntity<List<CompleteSystemDto>> retrieveCompleteSystemDataByFabId(@RequestParam(value = "fab") List<String> fabList)
    {

        return ResponseEntity.ok(systemServiceForHost.retrieveCompleteSystemDataByFabId(fabList));
    }

    @GetMapping("/api/criteria")
    public ResponseEntity<List<CompleteApiDto>> retrieveCompleteApiDataByFabId(@RequestParam(value = "fab") List<String> fabList)
    {

        return ResponseEntity.ok(apiServiceForHost.retrieveCompleteApiDataByFabId(fabList));
    }

    @GetMapping("/role/criteria")
    public ResponseEntity<List<CompleteRoleDto>> retrieveCompleteRoleDataByFabId(@RequestParam(value = "fab") List<String> fabList)
    {

        return ResponseEntity.ok(roleServiceForHost.retrieveCompleteRoleDataByFabId(fabList));
    }

    @GetMapping("/role/device/criteria")
    public ResponseEntity<List<RoleDeviceEntity>> retrieveRoleDeviceDataByFabId(@RequestParam(value = "fab") List<String> fabList)
    {

        return ResponseEntity.ok(roleDeviceServiceForHost.retrieveRoleDeviceDataByFabId(fabList));
    }

    @GetMapping("/apikey/criteria")
    public ResponseEntity<List<CompleteApikeyDto>> retrieveCompleteApikeyDataByFabId(@RequestParam(value = "fab") List<String> fabList)
    {

        return ResponseEntity.ok(apikeyServiceForHost.retrieveCompleteApikeyDataByFabId(fabList));
    }

    @GetMapping("/proxy")
    public ResponseEntity<AllProxyDataDto> retrieveAllProxyData()
    {

        return ResponseEntity.ok(proxyServiceForHost.retrieveAllProxyData());
    }
}
