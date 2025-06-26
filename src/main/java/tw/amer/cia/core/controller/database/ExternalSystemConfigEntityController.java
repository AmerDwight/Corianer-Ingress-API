package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.ExternalSystemConfigEntity;
import tw.amer.cia.core.service.database.ExternalSystemConfigEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/ExternalSystemConfigEntity")
public class ExternalSystemConfigEntityController {
    @Autowired
    ExternalSystemConfigEntityService externalSystemConfigEntityService;

    @GetMapping
    public ResponseEntity<List<ExternalSystemConfigEntity>> getAll() {
        return ResponseEntity.ok(externalSystemConfigEntityService.getAll());
    }

    @GetMapping("/{entityId}")
    public ResponseEntity<ExternalSystemConfigEntity> getById(@PathVariable String entityId) {
        return externalSystemConfigEntityService.getById(entityId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExternalSystemConfigEntity> create(@RequestBody ExternalSystemConfigEntity entity) {
        return ResponseEntity.ok(externalSystemConfigEntityService.create(entity));
    }

    @PutMapping("/{entityId}")
    public ResponseEntity<ExternalSystemConfigEntity> update(@PathVariable String entityId, @RequestBody ExternalSystemConfigEntity proxy) throws DataSourceAccessException {
        return ResponseEntity.ok(externalSystemConfigEntityService.update(entityId, proxy));
    }

    @PutMapping
    @PatchMapping
    public void createOrUpdateExtEntity(@RequestBody ExternalSystemConfigEntity entity) throws DataSourceAccessException {
        externalSystemConfigEntityService.createOrUpdateExtEntity(entity);
    }

    @DeleteMapping("/{entityId}")
    public ResponseEntity<Void> delete(@PathVariable String entityId) {
        externalSystemConfigEntityService.delete(entityId);
        return ResponseEntity.ok().build();
    }
}
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//class CreateEntityDto{
//    @JsonProperty("EXT_ENTITY_ID")
//    private String extEntityId;
//    @JsonProperty("EXT_ENTITY_KEY")
//    private String extEntityKey;
//}