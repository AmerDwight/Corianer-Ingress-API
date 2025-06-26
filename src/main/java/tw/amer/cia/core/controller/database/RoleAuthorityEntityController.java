package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.RoleAuthorityEntity;
import tw.amer.cia.core.model.database.compositeId.RoleAuthoroityEntityId;
import tw.amer.cia.core.service.database.RoleAuthorityEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/RoleAuthorityEntity")
public class RoleAuthorityEntityController
{
    @Autowired
    RoleAuthorityEntityService roleAuthorityEntityService;

    @GetMapping
    public ResponseEntity<List<RoleAuthorityEntity>> getAllRoleAuthorities()
    {
        return ResponseEntity.ok(roleAuthorityEntityService.getAllRoleAuthorities());
    }

    @GetMapping("/{roleId}/{apiId}/{fabId}")
    public ResponseEntity<RoleAuthorityEntity> getRoleAuthorityById(@PathVariable String roleId, @PathVariable String apiId, @PathVariable String fabId)
    {
        RoleAuthoroityEntityId id = new RoleAuthoroityEntityId(roleId, apiId, fabId);
        return roleAuthorityEntityService.getRoleAuthorityById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RoleAuthorityEntity> createRoleAuthority(@RequestBody RoleAuthorityEntity roleAuthority)
    {
        return ResponseEntity.ok(roleAuthorityEntityService.createRoleAuthority(roleAuthority));
    }

    @PostMapping("/batch")
    @Transactional
    public ResponseEntity<Boolean> createRoleAuthorityInBatch(@RequestBody List<RoleAuthorityEntity> onGrantAuthorityList)
    {
        boolean success = true;
        for (RoleAuthorityEntity roleAuthority : onGrantAuthorityList)
        {
            roleAuthorityEntityService.createRoleAuthority(roleAuthority);
        }
        return ResponseEntity.ok(success);
    }

    @PutMapping("/{roleId}/{apiId}/{fabId}")
    public ResponseEntity<RoleAuthorityEntity> updateRoleAuthority(@PathVariable String roleId, @PathVariable String apiId, @PathVariable String fabId, @RequestBody RoleAuthorityEntity roleAuthority) throws DataSourceAccessException
    {
        return ResponseEntity.ok(roleAuthorityEntityService.updateRoleAuthority(roleId, apiId, fabId, roleAuthority));
    }

    @DeleteMapping("/{roleId}/{apiId}/{fabId}")
    public ResponseEntity<Void> deleteRoleAuthority(@PathVariable String roleId, @PathVariable String apiId, @PathVariable String fabId)
    {
        roleAuthorityEntityService.deleteRoleAuthority(roleId, apiId, fabId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/batch")
    @Transactional
    public ResponseEntity<Boolean> deleteAuthorityInBatch(@RequestBody List<RoleAuthorityEntity> onRevokeAuthorityList)
    {
        boolean success = true;
        for (RoleAuthorityEntity roleAuthority : onRevokeAuthorityList)
        {
            roleAuthorityEntityService.deleteRoleAuthority(roleAuthority.getRoleId(), roleAuthority.getApiId(), roleAuthority.getFabId());

        }
        return ResponseEntity.ok(success);
    }
}
