package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.SystemDpyEntity;
import tw.amer.cia.core.model.database.compositeId.SystemDpyEntityId;
import tw.amer.cia.core.service.database.SystemDpyEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/SystemDpyEntity")
public class SystemDpyEntityController
{
    @Autowired
    SystemDpyEntityService systemDpyEntityService;

    @GetMapping
    public ResponseEntity<List<SystemDpyEntity>> getAllFabSystems()
    {
        return ResponseEntity.ok(systemDpyEntityService.getAllFabSystems());
    }

    @GetMapping("/{systemId}/{fabId}")
    public ResponseEntity<SystemDpyEntity> getFabSystemById(@PathVariable String systemId, @PathVariable String fabId)
    {
        SystemDpyEntityId cFabSysId = new SystemDpyEntityId(systemId, fabId);
        return systemDpyEntityService.getFabSystemById(cFabSysId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SystemDpyEntity> createFabSystem(@RequestBody SystemDpyEntity fabSystem)
    {
        return ResponseEntity.ok(systemDpyEntityService.createFabSystem(fabSystem));
    }

    @PutMapping("/{systemId}/{fabId}")
    public ResponseEntity<SystemDpyEntity> updateFabSystem(@PathVariable String systemId, @PathVariable String fabId, @RequestBody SystemDpyEntity fabSystem) throws DataSourceAccessException
    {
        return ResponseEntity.ok(systemDpyEntityService.updateFabSys(systemId, fabId, fabSystem));
    }

    @DeleteMapping("/{systemId}/{fabId}")
    public ResponseEntity<Void> deleteFabSystem(@PathVariable String systemId, @PathVariable String fabId)
    {
        SystemDpyEntityId cFabSysId = new SystemDpyEntityId(systemId, fabId);
        systemDpyEntityService.deleteFabSystem(cFabSysId);
        return ResponseEntity.ok().build();
    }
}