package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.ApiGwPluginDpyEntity;
import tw.amer.cia.core.model.database.compositeId.ApiGwPluginDpyEntityId;
import tw.amer.cia.core.service.database.ApiGwPluginDpyEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/ApiGwPluginDpyEntity")
public class ApiGwPluginDpyEntityController
{
    @Autowired
    ApiGwPluginDpyEntityService apiGwPluginDpyEntityService;

    @GetMapping
    public ResponseEntity<List<ApiGwPluginDpyEntity>> getAll()
    {
        return ResponseEntity.ok(apiGwPluginDpyEntityService.getAll());
    }

    @GetMapping("/{apiId}/{fabId}/{gwPluginId}")
    public ResponseEntity<ApiGwPluginDpyEntity> getById(@PathVariable String apiId, @PathVariable String fabId, @PathVariable String gwPluginId)
    {
        return apiGwPluginDpyEntityService.getById(ApiGwPluginDpyEntityId.builder()
                        .apiId(apiId)
                        .fabId(fabId)
                        .gwPluginId(gwPluginId)
                        .build())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiGwPluginDpyEntity> create(@RequestBody ApiGwPluginDpyEntity entity)
    {
        return ResponseEntity.ok(apiGwPluginDpyEntityService.create(entity));
    }

    @PutMapping("/{hostname}")
    public ResponseEntity<ApiGwPluginDpyEntity> update(@PathVariable ApiGwPluginDpyEntityId targetId, @RequestBody ApiGwPluginDpyEntity proxy) throws DataSourceAccessException
    {
        return ResponseEntity.ok(apiGwPluginDpyEntityService.update(targetId, proxy));
    }

    @DeleteMapping("/{hostname}")
    public ResponseEntity<Void> delete(@PathVariable ApiGwPluginDpyEntityId targetId)
    {
        apiGwPluginDpyEntityService.delete(targetId);
        return ResponseEntity.ok().build();
    }
}