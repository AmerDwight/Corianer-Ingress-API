package tw.amer.cia.core.controller.host.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tw.amer.cia.core.common.WebConstantLib;
import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.component.structural.annotation.RequireAdminUserVerifyApi;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.pojo.service.host.web.manager.contentManagement.Web_SiteOperateChargerDto;
import tw.amer.cia.core.service.host.web.Web_ManagerContentManageService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@HostRestController
@RequireAdminUserVerifyApi
@RequestMapping("/web/manager/contentManagement")
public class ManagerContentManageController {

    @Autowired
    Web_ManagerContentManageService contentManageService;

    private final String USERID_HEADER = WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID;

    @GetMapping("/list/system")
    public Object listSystemsForContentManage() {
        return contentManageService.listSystemsForContentManage();
    }

    @GetMapping("/list/api/{systemId}")
    public Object listMsForContentManageBySystemId(@PathVariable("systemId") String systemId) throws DataSourceAccessException {
        return contentManageService.listApiForContentManageBySystemId(systemId);
    }

    @GetMapping("/site/operate/charger")
    public Object listSiteOperateCharger() {
        return contentManageService.listSiteOperateCharger();
    }

    @PatchMapping("/site/operate/charger")
    public void updateSiteOperateCharger(@Valid @RequestBody Collection<Web_SiteOperateChargerDto> inDto) throws DataSourceAccessException {
        contentManageService.updateSiteOperateCharger(inDto);
    }

    @GetMapping("/list/user/id/{userId}")
    public Object listUserByUserIdLike(@PathVariable("userId") String userId) {
        return contentManageService.listUserByUserIdLike(userId);
    }

    @GetMapping("/info/site/by/fab/{fabId}")
    public Object getSiteInfoByFabId(@PathVariable("fabId") String fabId) throws DataSourceAccessException {
        return contentManageService.getSiteInfoByFabId(fabId);
    }

    @GetMapping("/attribute/status")
    public Object getContentAttributeStatus() throws DataSourceAccessException {
        return contentManageService.getContentAttributeStatus();
    }
}
