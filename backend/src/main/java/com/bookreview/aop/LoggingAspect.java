package com.bookreview.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.bookreview..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Entering method: {}.{} with args: {}", 
            joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(),
            joinPoint.getArgs());
    }

    @After("execution(* com.bookreview..*(..))")
    public void logAfter(JoinPoint joinPoint) {
        logger.info("Exiting method: {}.{}", 
            joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName());
    }
}
