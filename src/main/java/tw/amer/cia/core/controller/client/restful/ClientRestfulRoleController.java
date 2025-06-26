package tw.amer.cia.core.controller.client.restful;

import tw.amer.cia.core.component.structural.annotation.ClientRestController;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.exception.gateway.GatewayControllerException;
import tw.amer.cia.core.model.database.RoleDeviceEntity;
import tw.amer.cia.core.model.database.RoleEntity;
import tw.amer.cia.core.model.database.compositeId.RoleAuthoroityEntityId;
import tw.amer.cia.core.service.client.RoleDeviceServiceForClient;
import tw.amer.cia.core.service.client.RoleServiceForClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@ClientRestController
@RequestMapping("/${coriander-ingress-api.client.display-name}")
public class ClientRestfulRoleController
{
    @Autowired
    RoleServiceForClient roleServiceForClient;

    @Autowired
    RoleDeviceServiceForClient roleDeviceServiceForClient;

    @PostMapping("/restful/admin/role")
    public void createOrUpdateRole(@Valid @RequestBody RoleEntity inDto)
    {
        // Client 提供對單一角色的建置或更新操作。
        roleServiceForClient.createOrUpdateRole(inDto);
    }

    @DeleteMapping("/restful/admin/role/authority")
    public void deleteRoleAuthority(@Valid @RequestBody List<RoleAuthoroityEntityId> inDto)
    {
        // Client 提供對角色權限的刪除操作。
        roleServiceForClient.deleteRoleAuthority(inDto);
    }

    @PostMapping("/restful/admin/role/device")
    public void createOrUpdateRoleDevice(@Valid @RequestBody RoleDeviceEntity inDto) throws DataSourceAccessException, GatewayControllerException {
        // Client 提供對角色設備的建置或更新操作。
        roleDeviceServiceForClient.createOrUpdateRoleDevice(inDto);
    }

    @DeleteMapping("/restful/admin/role/device/{deviceId}")
    public void deleteRoleDeviceById(@PathVariable String deviceId) throws GatewayControllerException {
        // Client 提供對角色設備的刪除操作。
        roleDeviceServiceForClient.deleteRoleDeviceById(deviceId);
    }

}
