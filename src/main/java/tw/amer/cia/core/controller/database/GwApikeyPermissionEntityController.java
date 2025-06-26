package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.GwApikeyPermissionEntity;
import tw.amer.cia.core.service.database.GwApikeyPermissionEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/GwApikeyPermissionEntity")
public class GwApikeyPermissionEntityController
{
    @Autowired
    GwApikeyPermissionEntityService gwApikeyPermissionEntityService;

    @GetMapping
    public ResponseEntity<List<GwApikeyPermissionEntity>> getAllPermissions()
    {
        return ResponseEntity.ok(gwApikeyPermissionEntityService.getAllPermissions());
    }

    @GetMapping("/{apiKeyId}/{apiId}/{fabId}")
    public ResponseEntity<GwApikeyPermissionEntity> getPermissionById(@PathVariable String apiKeyId, @PathVariable String apiId, @PathVariable String fabId)
    {
        return gwApikeyPermissionEntityService.getPermissionById(apiKeyId, apiId, fabId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GwApikeyPermissionEntity> createPermission(@RequestBody GwApikeyPermissionEntity permission)
    {
        return ResponseEntity.ok(gwApikeyPermissionEntityService.createPermission(permission));
    }

    @PutMapping("/{apiKeyId}/{apiId}/{fabId}")
    public ResponseEntity<GwApikeyPermissionEntity> updatePermission(@PathVariable String apiKeyId, @PathVariable String apiId, @PathVariable String fabId, @RequestBody GwApikeyPermissionEntity permission) throws DataSourceAccessException
    {
        return ResponseEntity.ok(gwApikeyPermissionEntityService.updateKeyPermission(apiKeyId, apiId, fabId, permission));
    }

    @PostMapping("/batch")
    @Transactional
    public ResponseEntity<Boolean> createPermissionInBatch(@RequestBody List<GwApikeyPermissionEntity> permissions)
    {
        boolean success = true;
        for (GwApikeyPermissionEntity keyPermission : permissions)
        {
            gwApikeyPermissionEntityService.createPermission(keyPermission);
        }
        return ResponseEntity.ok(success);
    }


    @DeleteMapping("/{apiKeyId}/{apiId}/{fabId}")
    public ResponseEntity<Void> deletePermission(@PathVariable String apiKeyId, @PathVariable String apiId, @PathVariable String fabId)
    {
        gwApikeyPermissionEntityService.deletePermission(apiKeyId, apiId, fabId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/batch")
    @Transactional
    public ResponseEntity<Boolean> deletePermissionInBatch(@RequestBody List<GwApikeyPermissionEntity> permissions)
    {
        boolean success = true;
        for (GwApikeyPermissionEntity keyPermission : permissions)
        {
            gwApikeyPermissionEntityService.deletePermission(keyPermission.getApikeyId(),
                    keyPermission.getApiId(), keyPermission.getFabId());

        }
        return ResponseEntity.ok(success);
    }
}
