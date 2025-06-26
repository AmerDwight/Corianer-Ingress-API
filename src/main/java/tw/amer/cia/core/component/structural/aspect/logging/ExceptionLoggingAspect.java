package tw.amer.cia.core.component.structural.aspect.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class ExceptionLoggingAspect {

    @AfterThrowing(pointcut = "within(tw.amer.cia..*)", throwing = "ex")
    public void logRuntimeException(JoinPoint joinPoint, RuntimeException ex) {
        log.error("RuntimeException Captured： {}.{}() with cause = '{}' and arguments = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                ex.getCause() != null ? ex.getCause() : "NULL",
                Arrays.toString(joinPoint.getArgs()));
    }
}