package tw.amer.cia.core.component.structural.aspect.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ClientServiceLoggingAspect
{
    // Pointcut towards @ClientService component
    @Pointcut("@within(tw.amer.cia.core.component.structural.annotation.ClientService) && execution(* *(..))")
    public void clientServiceMethods()
    {
    }

    // Advice Before Method
    @Before("clientServiceMethods()")
    public void logClientMethodCall(JoinPoint joinPoint)
    {
        log.info("Calling @ClientService method: {} with arguments: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    // Advice After Method Success
    @AfterReturning(pointcut = "clientServiceMethods()", returning = "result")
    public void logClientMethodReturn(JoinPoint joinPoint, Object result)
    {
        log.info("@ClientService method {} completed successfully with result: {}", joinPoint.getSignature().toShortString(), result);
    }

    // Advice While Error Occurred
    @AfterThrowing(pointcut = "clientServiceMethods()", throwing = "error")
    public void logClientMethodError(JoinPoint joinPoint, Throwable error)
    {
        log.error("@ClientService method {} failed with error: {}", joinPoint.getSignature().toShortString(), error.getMessage());
    }

}
