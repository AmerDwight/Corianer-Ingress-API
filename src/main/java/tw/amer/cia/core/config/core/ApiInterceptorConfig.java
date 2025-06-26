package tw.amer.cia.core.config.core;

import tw.amer.cia.core.component.structural.interceptor.AdminRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiInterceptorConfig implements WebMvcConfigurer
{

    @Autowired
    AdminRequestInterceptor adminRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(adminRequestInterceptor).addPathPatterns("/database/**");
    }
}
