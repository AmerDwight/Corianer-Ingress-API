package tw.amer.cia.core.controller.host.web;

import tw.amer.cia.core.common.WebConstantLib;
import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.component.structural.annotation.RequireUserVerifyApi;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.service.host.web.HostWebFrontApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;


@HostRestController
@RequireUserVerifyApi
@RequestMapping("/web/info/detail")
public class DetailInfoController {

    @Autowired
    HostWebFrontApiService hostWebFrontApiService;


    @GetMapping("/system/{systemId}")
    public Object webApiMallFindDetailSystemInfoBySystemId(@PathVariable String systemId,
                                                           @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                           @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId) throws DataSourceAccessException {
        return hostWebFrontApiService.webGetSystemDetailInfo(systemId);
    }

    @GetMapping("/api/{apiId}")
    public Object webApiMallFindDetailApiInfoByApiId(@PathVariable String apiId,
                                                     @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                     @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId) throws DataSourceAccessException {
        return hostWebFrontApiService.webGetApiDetailInfo(roleId, apiId);
    }
}
