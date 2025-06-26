package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.GwRouteEntity;
import tw.amer.cia.core.service.database.GwRouteEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/GwRouteEntity")
public class GwRouteEntityController
{
    @Autowired
    GwRouteEntityService gwRouteEntityService;

    @GetMapping
    public ResponseEntity<List<GwRouteEntity>> getAllRoutes()
    {
        return ResponseEntity.ok(gwRouteEntityService.getAllRoutes());
    }

    @GetMapping("/{gwRouteId}")
    public ResponseEntity<GwRouteEntity> getRouteById(@PathVariable String gwRouteId)
    {
        return gwRouteEntityService.getRouteById(gwRouteId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GwRouteEntity> createRoute(@RequestBody GwRouteEntity route)
    {
        return ResponseEntity.ok(gwRouteEntityService.createRoute(route));
    }

    @PutMapping("/{gwRouteId}")
    public ResponseEntity<GwRouteEntity> updateRoute(@PathVariable String gwRouteId, @RequestBody GwRouteEntity route) throws DataSourceAccessException
    {
        return ResponseEntity.ok(gwRouteEntityService.updateRoute(gwRouteId, route));
    }

    @DeleteMapping("/{gwRouteId}")
    public ResponseEntity<Void> deleteRoute(@PathVariable String gwRouteId)
    {
        gwRouteEntityService.deleteRoute(gwRouteId);
        return ResponseEntity.ok().build();
    }
}
