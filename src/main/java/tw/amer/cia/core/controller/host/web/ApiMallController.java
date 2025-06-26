package tw.amer.cia.core.controller.host.web;

import tw.amer.cia.core.common.WebConstantLib;
import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.component.structural.annotation.RequireUserVerifyApi;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.service.host.web.HostWebFrontApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@HostRestController
@RequireUserVerifyApi
@RequestMapping("/web/apiMall")
public class ApiMallController {

    @Autowired
    HostWebFrontApiService hostWebFrontApiService;

    // Query
    @GetMapping("/systemCard/all")
    public Object webApiMallFindSystemCardAll(@RequestParam(defaultValue = "0") int pageNumber,
                                              @RequestParam(defaultValue = "10") int pageSize,
                                              @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                              @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId) {
        return hostWebFrontApiService.webFindSystemCardAll(pageNumber,pageSize);
    }

    @GetMapping("/apiCard/{systemId}")
    public Object webApiMallFindApiCardBySystemId(@PathVariable String systemId,
                                                  @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId,
                                                  @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID) String roleId) throws DataSourceAccessException {
        return hostWebFrontApiService.webFindApiCardBySystemId(roleId,systemId);
    }
}
