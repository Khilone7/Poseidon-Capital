package com.poseidoncapital.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LogManager.getLogger(LoggingAspect.class);

    /**
     * Pointcut targeting all classes in the com.pay_my_buddy package and its subpackages.
     *
     * <p>Used to apply logging around methods of the application's layers.</p>
     */
    @Pointcut("within(com.poseidoncapital..*)")
    public void appLayers() {}

    /**
     * Around advice applied to methods matched by the appLayers pointcut.
     *
     * <p>Logs an entry message before method execution. After execution, logs an exit
     * message including the elapsed time in milliseconds. If an exception occurs,
     * it is logged and rethrown.</p>
     *
     * @param joinPoint the intercepted join point providing the method context
     * @return the value returned by the intercepted method
     * @throws Throwable if the intercepted method throws an exception
     */
    @Around("appLayers()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // On récupère les infos de la méthode
        Signature signature = joinPoint.getSignature();
        Class<?> declaringType = signature.getDeclaringType();
        String className = declaringType.getSimpleName();
        String methodName = signature.getName();
        String layer = resolveLayer(declaringType);

        long start = System.currentTimeMillis();

        logger.info("[{}] → {}.{}()", layer, className, methodName);

        try {
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - start;

            logger.info("[{}] ← {}.{}() ({} ms)", layer, className, methodName, duration);

            return result;
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - start;

            logger.error("[{}] ✖ {}.{}() a levé {} après {} ms : {}",
                    layer, className, methodName, ex.getClass().getSimpleName(), duration, ex.getMessage(), ex);

            throw ex;
        }
    }

    /**
     * Resolves the application layer name based on the package of the given class.
     *
     * <p>Returns "CONTROLLER" if the package contains ".controller", "SERVICE" if
     * it contains ".service", "REPOSITORY" if it contains ".repository", and
     * "OTHER" otherwise.</p>
     *
     * @param type the class for which to determine the application layer
     * @return a string representing the application layer
     */
    private String resolveLayer(Class<?> type) {
        String packageName = type.getPackageName();
        if (packageName.contains(".controller")) {
            return "CONTROLLER";
        } else if (packageName.contains(".service")) {
            return "SERVICE";
        } else if (packageName.contains(".repository")) {
            return "REPOSITORY";
        } else {
            return "OTHER";
        }
    }
}