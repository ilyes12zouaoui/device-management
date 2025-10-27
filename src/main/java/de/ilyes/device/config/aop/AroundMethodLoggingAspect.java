package de.ilyes.device.config.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AroundMethodLoggingAspect {
  private static final Logger log = LoggerFactory.getLogger(AroundMethodLoggingAspect.class);

  @Around("within(de.ilyes.device.resource..*)")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
      log.info("Started execution of method: {}", joinPoint.getSignature().toShortString());
      try {
          Object proceed = joinPoint.proceed();
          log.info("Completed execution of method: {}", joinPoint.getSignature().toShortString());
          return proceed;
      } catch (Throwable ex) {
          log.error("Completed with error execution of method: {}", joinPoint.getSignature().toShortString(), ex);
          throw ex;
      }
  }
}
