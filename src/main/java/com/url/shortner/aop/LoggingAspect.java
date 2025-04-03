package com.url.shortner.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.url.shortner.service..*.*(..))")

    public void logBeforeMethodExecution() {
        logger.info("🔹 Method execution started...");
    }

    @AfterReturning("execution(* com.url.shortner.service..*.*(..))")
    public void logAfterReturning() {
        logger.info("✅ Method executed successfully...");
    }


    @AfterThrowing("execution(* com.url.shortner.service..*.*(..))")
    public void logAfterException() {
        logger.error("❌ An exception occurred during method execution...");
    }


    @Around("execution(* com.url.shortner.service..*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;
        logger.info("⏳ Method {} executed in {} ms", joinPoint.getSignature(), executionTime);
        return result;
    }

}
