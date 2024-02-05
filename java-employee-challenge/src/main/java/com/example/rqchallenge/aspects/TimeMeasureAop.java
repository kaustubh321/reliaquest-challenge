package com.example.rqchallenge.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
@Component
@Aspect
@Slf4j
public class TimeMeasureAop {
    @Around("@annotation(com.example.rqchallenge.aspects.Timed)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        log.info("{} executed in {} ms.", joinPoint.getSignature(), (System.currentTimeMillis()-startTime));
        return proceed;
    }
}