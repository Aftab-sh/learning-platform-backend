package com.learningplatform.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Aspect
public class LoggingAspect 
{
	
    @Around("execution(* com.learningplatform.service..*.*(..))")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("➡️  CALLING: {}.{}() with args: {}",
                className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();

        // Actual method execute hoga yahan
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();

        log.info("✅ COMPLETED: {}.{}() in {} ms",
                className, methodName, (endTime - startTime));

        return result;
    }
}
	
	
	
