package com.example.project.aop;

import com.example.project.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);
    private static final int SLOW_THRESHOLD_MS = 500;
    private static final int VERY_SLOW_THRESHOLD_MS = 1000;

    @Pointcut("within(@org.springframework.stereotype.Service *) || within(@org.springframework.web.bind.annotation.RestController *)")
    public void applicationLayerMethods() {
    }

    @Around("applicationLayerMethods()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String fullMethodName = className + "." + methodName;

        HttpServletRequest request = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            request = attributes.getRequest();
        }

        String requestContext = (request != null)
                ? String.format("[%s %s]", request.getMethod(), request.getRequestURI())
                : "[Internal]";

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(">>> Вход: {} Метод: {} | Аргументы: {}",
                    requestContext, fullMethodName, safeArgs(joinPoint.getArgs()));
        }

        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();

            long time = stopWatch.getTotalTimeMillis();
            logPerformance(fullMethodName, time);

            return result;

        } catch (ApiException apiEx) {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            LOGGER.warn("!!! Бизнес-ошибка в {}: {} (Статус: {})",
                    fullMethodName, apiEx.getMessage(), apiEx.getStatus());
            throw apiEx;

        } catch (Throwable throwable) {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            LOGGER.error("!!! Критическая ошибка в {}: {}", fullMethodName, throwable.getMessage());
            throw throwable;
        }
    }

    private String safeArgs(Object[] args) {
        try {
            return Arrays.toString(args);
        } catch (Exception _) {
            return "[unavailable]";
        }
    }
    
    private void logPerformance(String methodName, long time) {
        if (time > VERY_SLOW_THRESHOLD_MS) {
            LOGGER.warn("<<< {} за {} ms (ОЧЕНЬ МЕДЛЕННО)", methodName, time);
        } else if (time > SLOW_THRESHOLD_MS) {
            LOGGER.info("<<< {} за {} ms (МЕДЛЕННО)", methodName, time);
        } else {
            LOGGER.debug("<<< {} за {} ms", methodName, time);
        }
    }
}
