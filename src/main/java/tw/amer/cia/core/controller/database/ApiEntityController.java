package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.ApiEntity;
import tw.amer.cia.core.service.database.ApiEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/ApiEntity")
public class ApiEntityController
{

    @Autowired
    ApiEntityService apiEntityService;

    @GetMapping
    public ResponseEntity<List<ApiEntity>> getAllApi()
    {
        return ResponseEntity.ok(apiEntityService.getAllApi());
    }

    @GetMapping("/{apiId}")
    public ResponseEntity<ApiEntity> getApiById(@PathVariable String apiId)
    {
        return apiEntityService.getApiById(apiId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiEntity> createApi(@RequestBody ApiEntity api)
    {
        return ResponseEntity.ok(apiEntityService.createApi(api));
    }

    @PutMapping("/{apiId}")
    public ResponseEntity<ApiEntity> updateApi(@PathVariable String apiId, @RequestBody ApiEntity api) throws DataSourceAccessException
    {
        return ResponseEntity.ok(apiEntityService.updateApi(apiId, api));
    }

    @DeleteMapping("/{apiId}")
    public ResponseEntity<Void> deleteApi(@PathVariable String apiId)
    {
        apiEntityService.deleteApi(apiId);
        return ResponseEntity.ok().build();
    }
}
