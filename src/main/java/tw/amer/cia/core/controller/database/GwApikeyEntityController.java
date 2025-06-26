package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.GwApikeyEntity;
import tw.amer.cia.core.service.database.GwApikeyEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/GwApikeyEntity")
public class GwApikeyEntityController
{
    @Autowired
    GwApikeyEntityService gwApikeyEntityService;

    @GetMapping
    public ResponseEntity<List<GwApikeyEntity>> getAllApikeys()
    {
        return ResponseEntity.ok(gwApikeyEntityService.getAllApikeys());
    }

    @GetMapping("/{apiKeyId}")
    public ResponseEntity<GwApikeyEntity> getApikeyById(@PathVariable String apiKeyId)
    {
        return gwApikeyEntityService.getApikeyById(apiKeyId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GwApikeyEntity> createApikey(@RequestBody GwApikeyEntity apiKey)
    {
        return ResponseEntity.ok(gwApikeyEntityService.createApikey(apiKey));
    }

    @PutMapping("/{apiKeyId}")
    public ResponseEntity<GwApikeyEntity> updateApikey(@PathVariable String apiKeyId, @RequestBody GwApikeyEntity apiKey) throws DataSourceAccessException
    {
        return ResponseEntity.ok(gwApikeyEntityService.updateApiKey(apiKeyId, apiKey));
    }

    @DeleteMapping("/{apiKeyId}")
    public ResponseEntity<Void> deleteApikey(@PathVariable String apiKeyId)
    {
        gwApikeyEntityService.deleteApikey(apiKeyId);
        return ResponseEntity.ok().build();
    }
}
