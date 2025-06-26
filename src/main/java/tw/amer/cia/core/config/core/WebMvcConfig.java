package tw.amer.cia.core.config.core;

import tw.amer.cia.core.component.structural.httpMessageConvertor.YamlHttpMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    YamlHttpMessageConverter yamlHttpMessageConverter;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 全路徑通用
                .allowedOrigins("*") // 允許任何來源
                .allowedMethods("*") // 允許任何Http方法
                .allowedHeaders("*") // 允許任何Headers
                .allowCredentials(true).maxAge(3600);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("module new HttpMessageConverter: YamlHttpMessageConverter. Added.");
        converters.add(yamlHttpMessageConverter);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorParameter(true)
                .parameterName("format")
                .ignoreAcceptHeader(false)
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("yaml", YamlHttpMessageConverter.MEDIA_TYPE);
    }
}