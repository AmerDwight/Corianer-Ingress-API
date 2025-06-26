package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.RoleEntity;
import tw.amer.cia.core.service.database.RoleEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/RoleEntity")
public class RoleEntityController
{
    @Autowired
    RoleEntityService roleEntityService;

    @GetMapping
    public ResponseEntity<List<RoleEntity>> getAllRoles()
    {
        return ResponseEntity.ok(roleEntityService.getAllRoles());
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleEntity> getRoleById(@PathVariable String roleId)
    {
        return roleEntityService.getRoleById(roleId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RoleEntity> createRole(@RequestBody RoleEntity role)
    {
        return ResponseEntity.ok(roleEntityService.createRole(role));
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<RoleEntity> updateRole(@PathVariable String roleId, @RequestBody RoleEntity role) throws DataSourceAccessException
    {
        return ResponseEntity.ok(roleEntityService.updateRole(roleId, role));
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable String roleId)
    {
        roleEntityService.deleteRole(roleId);
        return ResponseEntity.ok().build();
    }
}
