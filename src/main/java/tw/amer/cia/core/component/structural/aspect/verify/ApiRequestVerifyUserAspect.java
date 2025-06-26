package tw.amer.cia.core.component.structural.aspect.verify;

import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.WebConstantLib;
import tw.amer.cia.core.component.structural.annotation.HostComponent;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.service.core.ValidateService;
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
public class ApiRequestVerifyUserAspect {
    @Autowired
    ValidateService validateService;

    @Pointcut("@within(tw.amer.cia.core.component.structural.annotation.RequireUserVerifyApi) || @annotation(tw.amer.cia.core.component.structural.annotation.RequireUserVerifyApi)")
    public void annotatedWithRequireUserVerifyApi() {}

    @Before("annotatedWithRequireUserVerifyApi()")
    public void checkHeaders(JoinPoint joinPoint) throws DataSourceAccessException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String userId = request.getHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID);
        String roleId = request.getHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID);

        if (userId == null || userId.trim().isEmpty()) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.GENERAL_API_MISSING_CRITICAL_DATA_INPUT.getCompleteMessage() +
                            WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID + " header is missing or empty");
        }
        if (roleId == null || roleId.trim().isEmpty()) {
            throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                    ErrorConstantLib.GENERAL_API_MISSING_CRITICAL_DATA_INPUT.getCompleteMessage() +
                            WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_ROLE_ID + " header is missing or empty");

        }
        validateService.validateUserIdMatchedRoleId(userId.trim(),roleId.trim());
    }
}
