package tw.amer.cia.core.component.structural.aspect.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class HostServiceLoggingAspect
{
    // Pointcut towards @HostService component
    @Pointcut("@within(tw.amer.cia.core.component.structural.annotation.HostService) && execution(* *(..))")
    public void hostServiceMethods()
    {
    }

    // Advice Before Method
    @Before("hostServiceMethods()")
    public void logHostMethodCall(JoinPoint joinPoint)
    {
        log.info("Calling @HostService method: {} with arguments: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    // Advice After Method Success
    @AfterReturning(pointcut = "hostServiceMethods()", returning = "result")
    public void logHostMethodReturn(JoinPoint joinPoint, Object result)
    {
        log.info("@HostService method {} completed successfully with result: {}", joinPoint.getSignature().toShortString(), result);
    }

    // Advice While Error Occurred
    @AfterThrowing(pointcut = "hostServiceMethods()", throwing = "error")
    public void logHostMethodError(JoinPoint joinPoint, Throwable error)
    {
        log.error("@HostService method {} failed with error: {}", joinPoint.getSignature().toShortString(), error.getMessage());
    }
}
