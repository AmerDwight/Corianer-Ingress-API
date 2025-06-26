package tw.amer.cia.core.controller.host.userAuth;

import tw.amer.cia.core.common.WebConstantLib;
import tw.amer.cia.core.component.structural.annotation.HostRestController;
import tw.amer.cia.core.model.pojo.service.common.user.UserBasicInfoDto;
import tw.amer.cia.core.model.pojo.service.common.user.UserLoginInfoDto;
import tw.amer.cia.core.service.host.auth.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@HostRestController
public class UserAuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @RequestMapping(value = "/Authentication/UserVerify")
    public ResponseEntity<Object> userVerify(@RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ACCOUNT) String account,
                                             @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_PASSWORD) String password) {
        try {

            UserBasicInfoDto rtnObject = authenticationService.userThirdPartyVerification(account, password);
            return new ResponseEntity<Object>(rtnObject, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/Authentication/UserLogin")
    public ResponseEntity<Object> userLogin(@RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ACCOUNT) String account,
                                            @RequestHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_PASSWORD) String password) {
        try {
            UserLoginInfoDto userLoginInfoDto = authenticationService.userCiaLogin(account, password);
            return new ResponseEntity<Object>(userLoginInfoDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
