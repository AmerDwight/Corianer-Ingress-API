package tw.amer.cia.core.controller.host.web;

import tw.amer.cia.core.common.WebConstantLib;
import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.component.structural.annotation.RequireAdminUserVerifyApi;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.service.host.web.Web_ManagerRealtimeGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@HostRestController
@RequireAdminUserVerifyApi
@RequestMapping("/web/manager/realtimeGateway")
public class ManagerRealtimeGatewayController {

    @Autowired
    Web_ManagerRealtimeGatewayService realtimeGatewayService;

    @GetMapping("/grafana/all")
    public Object getAllRealtimeGatewayGrafanaUrl(
            @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID) String userId) throws DataSourceAccessException {
        return realtimeGatewayService.getAllRealtimeGatewayGrafanaUrlBySite();
    }
}
