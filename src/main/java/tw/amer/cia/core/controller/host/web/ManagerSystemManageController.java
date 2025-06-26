package tw.amer.cia.core.controller.host.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.component.structural.annotation.RequireAdminUserVerifyApi;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.pojo.service.host.web.manager.systemManagement.Web_CreatePureSystemDto;
import tw.amer.cia.core.model.pojo.service.host.web.manager.systemManagement.Web_UpdateBasicSystemInfoDto;
import tw.amer.cia.core.model.pojo.service.host.web.manager.systemManagement.Web_UpdateSystemDeploymentDto;
import tw.amer.cia.core.service.host.web.Web_ManagerSystemManageService;

import javax.validation.Valid;

@Slf4j
@HostRestController
@RequireAdminUserVerifyApi
@RequestMapping("/web/manager/systemManagement")
public class ManagerSystemManageController {

    @Autowired
    Web_ManagerSystemManageService systemManageService;

    @GetMapping("/system/basic/{systemId}")
    public Object getBasicSystemInfoById(@PathVariable("systemId") String systemId) throws DataSourceAccessException {
        return systemManageService.getBasicSystemInfoById(systemId);
    }

    @GetMapping("/system/deploy/{systemId}")
    public Object getSystemDeployInfoById(@PathVariable("systemId") String systemId) throws DataSourceAccessException {
        return systemManageService.getSystemDeployInfoById(systemId);
    }

    @PutMapping("/system/basic")
    public void updateBasicSystemInfo(@Valid @RequestBody Web_UpdateBasicSystemInfoDto inDto) throws DataSourceAccessException, CiaProcessorException {
        systemManageService.updateBasicSystemInfo(inDto);
    }

    @GetMapping("/system/deploy/fab/available/{systemId}")
    public Object getSystemDeployAvailableFabIdBySystemId(@PathVariable("systemId") String systemId) throws DataSourceAccessException {
        return systemManageService.getSystemDeployAvailableFabIdBySystemId(systemId);
    }


    @PatchMapping("/system/deploy")
    public void createOrUpdateSystemDeploy(@Valid @RequestBody Web_UpdateSystemDeploymentDto inDto) throws DataSourceAccessException, CiaProcessorException {
        // 僅針對有異動的部分進行寫入即可
        // 刪除請用 Delete 方法
        systemManageService.createOrUpdateSystemDeploy(inDto);
    }

    @DeleteMapping("/system/deploy/{fabId}/{systemId}")
    public void deleteSystemDeploy(@PathVariable("fabId") String fabId,
                                   @PathVariable("systemId") String systemId) throws DataSourceAccessException, CiaProcessorException {
        systemManageService.deleteSystemDeploy(fabId, systemId);
    }

    @PostMapping("/system")
    public void createPureSystem(@Valid @RequestBody Web_CreatePureSystemDto inDto) throws DataSourceAccessException, CiaProcessorException {
        systemManageService.createPureSystem(inDto);
    }

    @DeleteMapping("/system/{systemId}")
    public void deleteSystemById(@PathVariable("systemId") String systemId) throws DataSourceAccessException, CiaProcessorException {
        systemManageService.deleteSystemById(systemId);
    }
}
