package tw.amer.cia.core.controller.host.control;

import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.component.structural.annotation.RequireExtEntityVerifyApi;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.model.pojo.service.host.control.MaintainRoleInHostDto;
import tw.amer.cia.core.service.host.api.HostAdminApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Slf4j
@HostRestController
@RequireExtEntityVerifyApi
@RequestMapping("/${coriander-ingress-api.host.display-name}")
public class RoleController
{
    @Autowired
    HostAdminApiService hostAdminApiService;
    @PostMapping("/role/maintainRole")
    public Object maintainRole(@Valid @RequestBody MaintainRoleInHostDto maintainRoleInDto) throws DataSourceAccessException, CiaProcessorException
    {
        Object obj = hostAdminApiService.maintainRole(maintainRoleInDto);
        if (obj != null)
        {
            return obj;
        } else
        {
            return ResponseEntity.noContent().build();
        }
    }
}
