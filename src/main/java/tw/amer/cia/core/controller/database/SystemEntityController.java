package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.SystemEntity;
import tw.amer.cia.core.service.database.SystemEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/SystemEntity")
public class SystemEntityController
{
    @Autowired
    SystemEntityService systemEntityService;

    @GetMapping
    public ResponseEntity<List<SystemEntity>> getAllSystems()
    {
        return ResponseEntity.ok(systemEntityService.getAllSystems());
    }

    @GetMapping("/{systemId}")
    public ResponseEntity<SystemEntity> getSystemById(@PathVariable String systemId)
    {
        return systemEntityService.getSystemById(systemId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SystemEntity> createSystem(@RequestBody SystemEntity system)
    {
        return ResponseEntity.ok(systemEntityService.createSystem(system));
    }

    @PutMapping("/{systemId}")
    public ResponseEntity<SystemEntity> updateSystem(@PathVariable String systemId, @RequestBody SystemEntity system) throws DataSourceAccessException
    {
        return ResponseEntity.ok(systemEntityService.updateSystem(systemId, system));
    }

    @DeleteMapping("/{systemId}")
    public ResponseEntity<Void> deleteSystem(@PathVariable String systemId)
    {
        systemEntityService.deleteSystem(systemId);
        return ResponseEntity.ok().build();
    }
}
