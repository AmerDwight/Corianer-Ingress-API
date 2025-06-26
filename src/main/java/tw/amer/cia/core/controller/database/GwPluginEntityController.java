package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.GwPluginEntity;
import tw.amer.cia.core.service.database.GwPluginEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/GwPluginEntity")
public class GwPluginEntityController
{

    @Autowired
    GwPluginEntityService gwPluginEntityService;

    @GetMapping
    public ResponseEntity<List<GwPluginEntity>> getAllPlugins()
    {
        return ResponseEntity.ok(gwPluginEntityService.getAllPlugins());
    }

    @GetMapping("/{pluginId}")
    public ResponseEntity<GwPluginEntity> getPluginById(@PathVariable String pluginId)
    {
        return gwPluginEntityService.getGwPluginById(pluginId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Object> createGwPlugin(@RequestBody GwPluginEntity gwPlugin)
    {
        return ResponseEntity.ok(gwPluginEntityService.createOrUpdateGwPlugin(gwPlugin));
    }

    @PutMapping("/{pluginId}")
    public ResponseEntity<GwPluginEntity> updateGwPlugin(@PathVariable String pluginId, @RequestBody GwPluginEntity gwPlugin) throws DataSourceAccessException
    {
        return ResponseEntity.ok(gwPluginEntityService.updateGwPlugin(pluginId, gwPlugin));
    }

    @DeleteMapping("/{pluginId}")
    public ResponseEntity<Void> deleteGwPlugin(@PathVariable String pluginId)
    {
        gwPluginEntityService.deleteGwPlugin(pluginId);
        return ResponseEntity.ok().build();
    }
}
