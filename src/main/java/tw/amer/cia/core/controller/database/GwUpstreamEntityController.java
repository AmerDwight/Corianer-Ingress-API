package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.GwUpstreamEntity;
import tw.amer.cia.core.service.database.GwUpstreamEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/GwUpstreamEntity")
public class GwUpstreamEntityController
{
    @Autowired
    GwUpstreamEntityService gwUpstreamEntityService;

    @GetMapping
    public ResponseEntity<List<GwUpstreamEntity>> getAllUpstreams()
    {
        return ResponseEntity.ok(gwUpstreamEntityService.getAllUpstreams());
    }

    @GetMapping("/{gwUsId}")
    public ResponseEntity<GwUpstreamEntity> getUpstreamById(@PathVariable String gwUsId)
    {
        return gwUpstreamEntityService.getUpstreamById(gwUsId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GwUpstreamEntity> createUpstream(@RequestBody GwUpstreamEntity upstream)
    {
        return ResponseEntity.ok(gwUpstreamEntityService.createUpstream(upstream));
    }

    @PutMapping("/{gwUsId}")
    public ResponseEntity<GwUpstreamEntity> updateUpstream(@PathVariable String gwUsId, @RequestBody GwUpstreamEntity upstream) throws DataSourceAccessException
    {
        return ResponseEntity.ok(gwUpstreamEntityService.updateUpstream(gwUsId, upstream));
    }

    @DeleteMapping("/{gwUsId}")
    public ResponseEntity<Void> deleteUpstream(@PathVariable String gwUsId)
    {
        gwUpstreamEntityService.deleteUpstream(gwUsId);
        return ResponseEntity.ok().build();
    }
}
