package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.ApiEndpointEntity;
import tw.amer.cia.core.service.database.ApiEndpointEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/ApiEndpointEntity")
public class ApiEndpointEntityController
{
    @Autowired
    ApiEndpointEntityService apiEndpointEntityService;

    @GetMapping
    public ResponseEntity<List<ApiEndpointEntity>> getAllApiEndpoints()
    {
        return ResponseEntity.ok(apiEndpointEntityService.getAllApiEndpoints());
    }

    @GetMapping("/{endpointId}")
    public ResponseEntity<ApiEndpointEntity> getApiEndpointById(@PathVariable String endpointId)
    {
        return apiEndpointEntityService.getApiEndpointById(endpointId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiEndpointEntity> createApiEndpoint(@RequestBody ApiEndpointEntity apiEndpoint)
    {
        return ResponseEntity.ok(apiEndpointEntityService.createApiEndpoint(apiEndpoint));
    }

    @PutMapping("/{endpointId}")
    public ResponseEntity<ApiEndpointEntity> updateApiEndpoint(@PathVariable String endpointId, @RequestBody ApiEndpointEntity apiEndpoint) throws DataSourceAccessException
    {
        return ResponseEntity.ok(apiEndpointEntityService.updateApiEndpoint(endpointId, apiEndpoint));
    }

    @DeleteMapping("/{endpointId}")
    public ResponseEntity<Void> deleteApiEndpoint(@PathVariable String endpointId)
    {
        apiEndpointEntityService.deleteApiEndpoint(endpointId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/batch/apiId/{apiId}")
    public ResponseEntity<Void> deleteApiEndpointByApiId(@PathVariable String apiId)
    {
        apiEndpointEntityService.deleteApiEndpointByApiId(apiId);
        return ResponseEntity.ok().build();
    }
}