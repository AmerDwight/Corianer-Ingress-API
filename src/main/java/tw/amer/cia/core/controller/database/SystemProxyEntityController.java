package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.SystemProxyEntity;
import tw.amer.cia.core.service.database.SystemProxyEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/SystemProxyEntity")
public class SystemProxyEntityController
{

    @Autowired
    SystemProxyEntityService systemProxyEntityService;

    @GetMapping
    public ResponseEntity<List<SystemProxyEntity>> getAllProxy()
    {
        return ResponseEntity.ok(systemProxyEntityService.getAll());
    }

    @GetMapping("/{proxyId}")
    public ResponseEntity<SystemProxyEntity> getById(@PathVariable String proxyId)
    {
        return systemProxyEntityService.getById(proxyId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SystemProxyEntity> create(@RequestBody SystemProxyEntity proxy)
    {
        return ResponseEntity.ok(systemProxyEntityService.create(proxy));
    }

    @PutMapping("/{proxyId}")
    public ResponseEntity<SystemProxyEntity> update(@PathVariable String proxyId, @RequestBody SystemProxyEntity proxy) throws DataSourceAccessException
    {
        return ResponseEntity.ok(systemProxyEntityService.update(proxyId, proxy));
    }

    @DeleteMapping("/{proxyId}")
    public ResponseEntity<Void> delete(@PathVariable String proxyId)
    {
        systemProxyEntityService.delete(proxyId);
        return ResponseEntity.ok().build();
    }
}
