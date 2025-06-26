package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.RoleDeviceEntity;
import tw.amer.cia.core.service.database.RoleDeviceEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/RoleDeviceEntity")
public class RoleDeviceEntityController
{
    @Autowired
    RoleDeviceEntityService roleDeviceEntityService;

    @GetMapping
    public ResponseEntity<List<RoleDeviceEntity>> getAllDevices()
    {
        return ResponseEntity.ok(roleDeviceEntityService.getAllDevices());
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<RoleDeviceEntity> getDeviceById(@PathVariable String deviceId)
    {
        return roleDeviceEntityService.getRoleDeviceById(deviceId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RoleDeviceEntity> createDevice(@RequestBody RoleDeviceEntity device)
    {
        return ResponseEntity.ok(roleDeviceEntityService.createDevice(device));
    }

    @PutMapping("/{deviceId}")
    public ResponseEntity<RoleDeviceEntity> updateDevice(@PathVariable String deviceId, @RequestBody RoleDeviceEntity device) throws DataSourceAccessException
    {
        return ResponseEntity.ok(roleDeviceEntityService.updateRoleDevice(deviceId, device));
    }

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> deleteDevice(@PathVariable String deviceId)
    {
        roleDeviceEntityService.deleteDevice(deviceId);
        return ResponseEntity.ok().build();
    }
}
