package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.RoleUserEntity;
import tw.amer.cia.core.model.database.compositeId.RoleUserEntityId;
import tw.amer.cia.core.service.database.RoleUserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/RoleUserEntity")
public class RoleUserEntityController
{
    @Autowired
    RoleUserEntityService roleUserEntityService;

    @GetMapping
    public ResponseEntity<List<RoleUserEntity>> getAllRoleUsers()
    {
        return ResponseEntity.ok(roleUserEntityService.getAllRoleUsers());
    }

    @GetMapping("/{userId}/{roleId}")
    public ResponseEntity<RoleUserEntity> getRoleUserById(@PathVariable String userId, @PathVariable String roleId)
    {
        RoleUserEntityId id = new RoleUserEntityId(userId, roleId);
        return roleUserEntityService.getRoleUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RoleUserEntity> createRoleUser(@RequestBody RoleUserEntity roleUser)
    {
        return ResponseEntity.ok(roleUserEntityService.createRoleUser(roleUser));
    }

    @PostMapping("/batch")
    @Transactional
    public ResponseEntity<Boolean> createRoleUsrInBatch(@RequestBody List<RoleUserEntity> addRoleUsrList)
    {
        boolean success = true;
        for (RoleUserEntity roleUser : addRoleUsrList)
        {
            roleUserEntityService.createRoleUser(roleUser);
        }
        return ResponseEntity.ok(success);
    }

    @PutMapping("/{userId}/{roleId}")
    public ResponseEntity<RoleUserEntity> updateRoleUser(@PathVariable String userId, @PathVariable String roleId, @RequestBody RoleUserEntity roleUser) throws DataSourceAccessException
    {
        return ResponseEntity.ok(roleUserEntityService.updateRoleUsr(userId, roleId, roleUser));
    }

    @DeleteMapping("/{userId}/{roleId}")
    public ResponseEntity<Void> deleteRoleUser(@PathVariable String userId, @PathVariable String roleId)
    {
        RoleUserEntityId id = new RoleUserEntityId(userId, roleId);
        roleUserEntityService.deleteRoleUser(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/batch")
    @Transactional
    public ResponseEntity<Boolean> deleteRoleUsrInBatch(@RequestBody List<RoleUserEntity> eraserRoleUsrList)
    {
        boolean success = true;
        for (RoleUserEntity roleUsr : eraserRoleUsrList)
        {
            roleUserEntityService.deleteRoleUser(new RoleUserEntityId(roleUsr.getUserId(), roleUsr.getRoleId()));

        }
        return ResponseEntity.ok(success);
    }
}
