package tw.amer.cia.core.component.structural.aspect;

import tw.amer.cia.core.component.structural.info.MethodCounter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ApiCallsCountingAspect
{
    @Autowired
    private MethodCounter methodCounter;

    @Pointcut("execution(* tw.amer.cia.core.controller..*(..))")
    public void controllerMethods()
    {
    }

    @Before("controllerMethods()")
    public void countingBeforeControllerMethod(org.aspectj.lang.JoinPoint joinPoint)
    {
        String methodName = joinPoint.getSignature().toShortString();
        log.info("Request Received: {}", methodName);
        methodCounter.incrementMethodCount(methodName);
    }
}
