package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.ApiDpyEntity;
import tw.amer.cia.core.model.database.compositeId.ApiDpyEntityId;
import tw.amer.cia.core.service.database.ApiDpyEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/ApiDpyEntity")
public class ApiDpyEntityController
{

    @Autowired
    ApiDpyEntityService apiDpyEntityService;

    @GetMapping
    public ResponseEntity<List<ApiDpyEntity>> getAllFabApi()
    {
        return ResponseEntity.ok(apiDpyEntityService.getAllFabApi());
    }

    @GetMapping("/{apiId}/{fabId}")
    public ResponseEntity<ApiDpyEntity> getFabApiById(@PathVariable String apiId, @PathVariable String fabId)
    {
        ApiDpyEntityId id = new ApiDpyEntityId(apiId, fabId);
        return apiDpyEntityService.getFabApiById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiDpyEntity> createFabApi(@RequestBody ApiDpyEntity fabApi)
    {
        return ResponseEntity.ok(apiDpyEntityService.createFabApi(fabApi));
    }


    @PutMapping("/{apiId}/{fabId}")
    public ResponseEntity<ApiDpyEntity> updateFabApi(@PathVariable String apiId, @PathVariable String fabId, @RequestBody ApiDpyEntity fabApi) throws DataSourceAccessException
    {
        return ResponseEntity.ok(apiDpyEntityService.updateFabApi(apiId, fabId, fabApi));
    }

    @DeleteMapping("/{apiId}/{fabId}")
    public ResponseEntity<Void> deleteFabApi(@PathVariable String apiId, @PathVariable String fabId)
    {
        ApiDpyEntityId id = new ApiDpyEntityId(apiId, fabId);
        apiDpyEntityService.deleteFabApi(id);
        return ResponseEntity.ok().build();
    }
}