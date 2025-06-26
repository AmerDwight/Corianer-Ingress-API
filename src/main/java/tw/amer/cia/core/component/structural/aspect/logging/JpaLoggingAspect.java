package tw.amer.cia.core.component.structural.aspect.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class JpaLoggingAspect
{
    // Aspect： Whenever methods in repository was called.
    @Pointcut("execution(* org.springframework.data.repository.Repository+.*(..)) && !execution(* *.toString(..))")
    public void repositoryMethods()
    {
    }

    // Before JPA Repository
    @Before("repositoryMethods()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            log.info("Calling JPA Method: " + joinPoint.getSignature().toShortString() + " with arguments: " + Arrays.deepToString(args));
        } else {
            log.info("Calling JPA Method: " + joinPoint.getSignature().toShortString() + " with no arguments");
        }
    }

    // When JPA Repository is successfully finished.
    @AfterReturning(pointcut = "repositoryMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("JPA Method successfully called: " + joinPoint.getSignature().toShortString());
    }

    // When JPA Repository throwing Exception
    @AfterThrowing(pointcut = "repositoryMethods()", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        log.error("Exception in JPA Method: " + joinPoint.getSignature().toShortString(), error);
    }

}
