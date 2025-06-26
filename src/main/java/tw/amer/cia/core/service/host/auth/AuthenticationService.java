package tw.amer.cia.core.service.host.auth;

import tw.amer.cia.core.common.RoleSetting;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.pojo.service.common.user.UserBasicInfoDto;
import tw.amer.cia.core.model.pojo.service.common.user.UserLoginInfoDto;

public interface AuthenticationService {
    String DEPT_ROLE_TYPE = RoleSetting.ROLE_TYPE.DEPT.name();

    UserBasicInfoDto userThirdPartyVerification(String inputId, String password) throws DataSourceAccessException;
    UserLoginInfoDto userCiaLogin(String inputId, String password) throws DataSourceAccessException;
}
