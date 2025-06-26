package tw.amer.cia.core.component.structural.aspect.verify;

import tw.amer.cia.core.common.ControlApiConstantLib;
import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.component.structural.annotation.HostComponent;
import tw.amer.cia.core.exception.DataSourceAccessException;
import tw.amer.cia.core.service.core.ValidateService;
import lombok.Setter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@HostComponent
public class ApiRequestVerifyExtEntityAspect {

    @Value("${CIA-VERIFY-CONTROL_API:false}")
    @Setter
    private boolean CIA_VERIFY_CONTROL_API;

    @Autowired
    ValidateService validateService;

    @Pointcut("@within(tw.amer.cia.core.component.structural.annotation.RequireExtEntityVerifyApi) || @annotation(tw.amer.cia.core.component.structural.annotation.RequireExtEntityVerifyApi)")
    public void annotatedWithRequireExtEntityVerifyApi() {
    }

    @Before("annotatedWithRequireExtEntityVerifyApi()")
    public void checkHeaders(JoinPoint joinPoint) throws DataSourceAccessException {
        if (CIA_VERIFY_CONTROL_API) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            String entityId = request.getHeader(ControlApiConstantLib.CONTROL_API_REQUEST_HEADER_IDENTIFIER_ID);
            String entityKey = request.getHeader(ControlApiConstantLib.CONTROL_API_REQUEST_HEADER_IDENTIFIER_KEY);

            if (entityId == null || entityId.trim().isEmpty()) {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.GENERAL_API_MISSING_CRITICAL_DATA_INPUT.getCompleteMessage() +
                                ControlApiConstantLib.CONTROL_API_REQUEST_HEADER_IDENTIFIER_ID + " header is missing or empty");
            }
            if (entityKey == null || entityKey.trim().isEmpty()) {
                throw DataSourceAccessException.createExceptionForHttp(HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.GENERAL_API_MISSING_CRITICAL_DATA_INPUT.getCompleteMessage() +
                                ControlApiConstantLib.CONTROL_API_REQUEST_HEADER_IDENTIFIER_KEY + " header is missing or empty");
            }
            validateService.validateExtEntityIdAndKey(entityId.trim(), entityKey.trim());
        }
    }
}
