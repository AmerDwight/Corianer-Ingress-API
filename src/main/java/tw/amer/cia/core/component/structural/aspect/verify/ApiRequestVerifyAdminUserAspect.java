package tw.amer.cia.core.component.structural.aspect.verify;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.GeneralSetting;
import tw.amer.cia.core.common.WebConstantLib;
import tw.amer.cia.core.component.structural.annotation.HostComponent;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.model.database.UserEntity;
import tw.amer.cia.core.service.core.ValidateService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@HostComponent
public class ApiRequestVerifyAdminUserAspect {
    @Autowired
    ValidateService validateService;

    @Pointcut("@within(tw.amer.cia.core.component.structural.annotation.RequireAdminUserVerifyApi) || @annotation(tw.amer.cia.core.component.structural.annotation.RequireAdminUserVerifyApi)")
    public void annotatedWithRequireAdminUserVerifyApi() {
    }

    @Before("annotatedWithRequireAdminUserVerifyApi()")
    public void checkHeaders(JoinPoint joinPoint) throws DataSourceAccessException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String userId = request.getHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID);

        if (userId == null || userId.trim().isEmpty()) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.GENERAL_API_MISSING_CRITICAL_DATA_INPUT.getCompleteMessage() +
                            WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID + " header is missing or empty");
        }
        UserEntity user = validateService.validateUserId(userId.trim());
        if (StringUtils.equalsIgnoreCase(user.getIsAdminGroup(), GeneralSetting.GENERAL_NEGATIVE_STRING)) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.UNAUTHORIZED,
                    ErrorConstantLib.WEB_MANAGER_UNAUTHORISED_USER.getCompleteMessage());
        }
    }
}
