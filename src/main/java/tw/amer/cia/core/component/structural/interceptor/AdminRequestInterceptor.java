package tw.amer.cia.core.component.structural.interceptor;

import tw.amer.cia.core.component.structural.property.CoreProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Data
@Component
public class AdminRequestInterceptor implements HandlerInterceptor
{

    @Autowired
    CoreProperties coreProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    {
        String adminApiKey = request.getHeader(coreProperties.getSetting().getApi().getControlHeader());
        if (coreProperties.getSetting().getApi().getAdminKey().contains(adminApiKey))
        {
            return true;
        } else
        {
            log.info("From Header： {} get Key: {}" , coreProperties.getSetting().getApi().getControlHeader(), adminApiKey);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}
