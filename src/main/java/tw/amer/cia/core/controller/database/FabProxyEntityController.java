package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.FabProxyEntity;
import tw.amer.cia.core.model.database.compositeId.FabProxyEntityId;
import tw.amer.cia.core.service.database.FabProxyEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/FabProxyEntity")
public class FabProxyEntityController
{
    @Autowired
    FabProxyEntityService fabProxyEntityService;

    @GetMapping
    public ResponseEntity<List<FabProxyEntity>> getAllFabSystems()
    {
        return ResponseEntity.ok(fabProxyEntityService.getAll());
    }

    @GetMapping("/{fabId}/{proxyId}")
    public ResponseEntity<FabProxyEntity> getById(@PathVariable String fabId, @PathVariable String proxyId)
    {
        FabProxyEntityId cFabProxyId = new FabProxyEntityId(fabId, proxyId);
        return fabProxyEntityService.getById(cFabProxyId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FabProxyEntity> create(@RequestBody FabProxyEntity proxy)
    {
        return ResponseEntity.ok(fabProxyEntityService.create(proxy));
    }

    @PutMapping("/{fabId}/{proxyId}")
    public ResponseEntity<FabProxyEntity> update(@PathVariable String fabId, @PathVariable String proxyId, @RequestBody FabProxyEntity proxy) throws DataSourceAccessException
    {
        return ResponseEntity.ok(fabProxyEntityService.update(fabId, proxyId, proxy));
    }

    @DeleteMapping("/{fabId}/{proxyId}")
    public ResponseEntity<Void> deleteFabSystem(@PathVariable String fabId, @PathVariable String proxyId)
    {
        FabProxyEntityId cFabProxyId = new FabProxyEntityId(fabId, proxyId);
        fabProxyEntityService.delete(cFabProxyId);
        return ResponseEntity.ok().build();
    }
}