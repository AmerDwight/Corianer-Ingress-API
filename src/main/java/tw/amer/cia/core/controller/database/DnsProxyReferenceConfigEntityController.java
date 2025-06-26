package tw.amer.cia.core.controller.database;

import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.DnsProxyReferenceConfigEntity;
import tw.amer.cia.core.service.database.DnsProxyReferenceConfigEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/database/sync/DnsProxyReferenceConfigEntity")
public class DnsProxyReferenceConfigEntityController
{
    @Autowired
    DnsProxyReferenceConfigEntityService dnsProxyReferenceConfigEntityService;

    @GetMapping
    public ResponseEntity<List<DnsProxyReferenceConfigEntity>> getAll()
    {
        return ResponseEntity.ok(dnsProxyReferenceConfigEntityService.getAll());
    }

    @GetMapping("/{hostname}")
    public ResponseEntity<DnsProxyReferenceConfigEntity> getById(@PathVariable String hostname)
    {
        return dnsProxyReferenceConfigEntityService.getById(hostname)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DnsProxyReferenceConfigEntity> create(@RequestBody DnsProxyReferenceConfigEntity entity)
    {
        return ResponseEntity.ok(dnsProxyReferenceConfigEntityService.create(entity));
    }

    @PutMapping("/{hostname}")
    public ResponseEntity<DnsProxyReferenceConfigEntity> update(@PathVariable String hostname, @RequestBody DnsProxyReferenceConfigEntity proxy) throws DataSourceAccessException
    {
        return ResponseEntity.ok(dnsProxyReferenceConfigEntityService.update(hostname, proxy));
    }

    @DeleteMapping("/{hostname}")
    public ResponseEntity<Void> delete(@PathVariable String hostname)
    {
        dnsProxyReferenceConfigEntityService.delete(hostname);
        return ResponseEntity.ok().build();
    }
}