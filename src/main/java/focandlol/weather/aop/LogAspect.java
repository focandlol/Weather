package focandlol.weather.aop;

import focandlol.weather.WeatherApplication;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {
    private final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    @Pointcut("execution(* focandlol.weather..*(..))")
    private void pointcut() {}

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info(joinPoint.getTarget() + "-> started to " + joinPoint.getSignature().getName());

        Object proceed = joinPoint.proceed();

        logger.info(joinPoint.getTarget() + "-> end to " + joinPoint.getSignature().getName());

        return proceed;
    }
}
