package tw.amer.cia.core.controller.host;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.exception.CiaProcessorException;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.service.host.GatewayInfoServiceForHost;
import tw.amer.cia.core.service.host.signOff.SignOffCompleteService;

@HostRestController
public class LiveUpdateController {

    @Autowired
    SignOffCompleteService signOffCompleteService;

    @Autowired
    GatewayInfoServiceForHost gatewayInfoServiceForHost;

    @PatchMapping(value = "/live-update/sign-off/complete/role-auth/{applyFormId}")
    public ResponseEntity<Object> autoCompleteRoleAuthorityGrantingByApplyFormId(@PathVariable String applyFormId) throws DataSourceAccessException, CiaProcessorException {
        signOffCompleteService.autoCompleteRoleAuthorityGrantingByApplyFormId(applyFormId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/live-update/gateway/info")
    public void liveUpdateExternalGatewayInfoFromClient() throws CiaProcessorException {
        gatewayInfoServiceForHost.updateExternalGatewayInfoFromClient();
    }

}
