package tw.amer.cia.core.controller.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.amer.cia.core.component.structural.jpa.specification.FabEntitySpecification;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.FabEntity;
import tw.amer.cia.core.service.database.FabEntityService;

import java.util.List;

@RestController
@RequestMapping("/database/sync/FabEntity")
public class FabEntityController {

    @Autowired
    FabEntityService fabEntityService;

    @GetMapping
    public ResponseEntity<List<FabEntity>> getAllFabs() {
        return ResponseEntity.ok(fabEntityService.getAllFabs());
    }

    @GetMapping("/criteria")
    public ResponseEntity<List<FabEntity>> getFabsByCriteria(@RequestParam(required = false, value = "fab") List<String> fabList,
                                                             @RequestParam(required = false, value = "site") List<String> siteList) {
        Specification<FabEntity> spec = Specification.where(FabEntitySpecification.fabIdIn(fabList))
                .and(FabEntitySpecification.siteIn(siteList));
        return ResponseEntity.ok(fabEntityService.getByCriteria(spec));
    }

    @GetMapping("/{fabId}")
    public ResponseEntity<FabEntity> getFabById(@PathVariable String fabId) {
        return fabEntityService.getFabById(fabId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FabEntity> createFab(@RequestBody FabEntity fab) {
        return ResponseEntity.ok(fabEntityService.createFab(fab));
    }

    @PutMapping("/{fabId}")
    public ResponseEntity<FabEntity> updateFab(@PathVariable String fabId, @RequestBody FabEntity fab) throws DataSourceAccessException {
        return ResponseEntity.ok(fabEntityService.updateFab(fabId, fab));
    }

    @DeleteMapping("/{fabId}")
    public ResponseEntity<Void> deleteFab(@PathVariable String fabId) {
        fabEntityService.deleteFab(fabId);
        return ResponseEntity.ok().build();
    }
}
