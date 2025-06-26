package tw.amer.cia.core.controller.host.restful;

import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.service.host.api.BackdoorApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@HostRestController
@RequestMapping("/backdoor")
public class BackdoorApiController
{
    @Autowired
    BackdoorApiService backdoorApiService;

    @PutMapping("/authority/grantAllAuthority/{roleId}")
    public ResponseEntity<Object> grantAllAuthorityForRole(@PathVariable String roleId)
    {
        backdoorApiService.liveUpdateSupremeAuthority(roleId);
        return ResponseEntity.ok().build();
    }
}
