package tw.amer.cia.core.component.structural.jpa;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tw.amer.cia.core.common.ControlApiConstantLib;
import tw.amer.cia.core.common.ErrorConstantLib;
import tw.amer.cia.core.common.WebConstantLib;
import tw.amer.cia.core.exception.DataSourceAccessException;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


@Component
@Slf4j
public class DatasourceAuditor implements AuditorAware<String> {

    @Value("${coriander-ingress-api.setting.deploy-type}")
    private String deployType;

    @Override
    public Optional<String> getCurrentAuditor() {
        // Get USER_ID From Request Header
        String responsibleId = getResponsibleIdByRequestHeader();
        if (StringUtils.isNotBlank(responsibleId)) {
            log.debug("Obtain User ID: {}", responsibleId);
            return Optional.of(responsibleId);
        }

        String defaultAuditor = "CIA.DEFAULT";

        log.debug("By Default: {}", defaultAuditor);
        return Optional.of(defaultAuditor);
    }

    private String getResponsibleIdByRequestHeader() {
        try {
            // From RequestContextHolder
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();

                String userId = request.getHeader(WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID);
                if (StringUtils.isNotBlank(userId)) {
                    return userId;
                }

                userId = request.getHeader(ControlApiConstantLib.CONTROL_API_REQUEST_HEADER_IDENTIFIER_ID);
                if (StringUtils.isNotBlank(userId)) {
                    return userId;
                }
            } else {
                throw DataSourceAccessException.createExceptionForHttp(
                        HttpStatus.BAD_REQUEST,
                        ErrorConstantLib.WEB_SIGN_OFF_APPLY_ROLE_AUTHORITY_APPLY_DATA_MISSING.getCompleteMessage()
                                + " Header: " + WebConstantLib.WEB_UI_CALL_API_REQUEST_HEADER_USER_ID
                                + " or " + ControlApiConstantLib.CONTROL_API_REQUEST_HEADER_IDENTIFIER_ID
                );
            }
        } catch (Exception e) {
            log.error("獲取用戶ID時發生錯誤");
        }

        return null;
    }
}
