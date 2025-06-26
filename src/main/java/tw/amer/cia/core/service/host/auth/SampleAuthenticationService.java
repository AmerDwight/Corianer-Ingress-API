package tw.amer.cia.core.service.host.auth;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.dao.RoleEntityRepo;
import tw.amer.cia.core.model.database.dao.UserEntityRepo;
import tw.amer.cia.core.model.pojo.service.common.user.UserBasicInfoDto;
import tw.amer.cia.core.model.pojo.service.common.user.UserLoginInfoDto;

@Data
@Slf4j
public class SampleAuthenticationService implements AuthenticationService {
    @Autowired
    RoleEntityRepo roleEntityRepo;

    @Autowired
    UserEntityRepo userEntityRepo;


    public SampleAuthenticationService() {
        // TODO: Constructor implementation
    }

    @Override
    public UserBasicInfoDto userThirdPartyVerification(String inputId, String password) throws DataSourceAccessException {
        // TODO: Implementation for third party verification
        return null;
    }

    @Override
    public UserLoginInfoDto userCiaLogin(String inputId, String password) throws DataSourceAccessException {
        // TODO: Implementation for login
        return null;
    }

    public String checkAndCreateFolder(String path, String folderName) {
        // TODO: Implementation for check and create folder
        return null;
    }
}
