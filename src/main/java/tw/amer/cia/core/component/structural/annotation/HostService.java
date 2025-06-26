package tw.amer.cia.core.component.structural.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service
@ConditionalOnProperty(name = "coriander-ingress-api.setting.deploy-type", havingValue = "host")
public @interface HostService
{
}
