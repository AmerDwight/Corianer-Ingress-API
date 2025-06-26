package tw.amer.cia.core.component.structural.aspect.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class GatewayControlLoggingAspect
{
    // Pointcut for GatewayControlHelper
    @Pointcut("within(tw.amer.cia.core.component.functional.gateway.GatewayControlHelper+)")
    public void gatewayCommanderMethods()
    {
    }

    // Advice for GatewayControlHelper
    @Before("gatewayCommanderMethods()")
    public void logGatewayCommanderMethodCall(JoinPoint joinPoint)
    {
        log.info("Calling GatewayControlHelper method: {} with arguments: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "gatewayCommanderMethods()", returning = "result")
    public void logGatewayCommanderMethodReturn(JoinPoint joinPoint, Object result)
    {
        log.info("GatewayControlHelper method {} completed with result: {}", joinPoint.getSignature().toShortString(), result);
    }

}
