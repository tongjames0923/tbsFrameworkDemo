package com.example.demo;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import tbs.framework.base.interfaces.IMethodInterceptHandler;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;

import java.lang.reflect.Method;

@Component
public class LogMethodIntercept implements IMethodInterceptHandler {

    @AutoLogger
    ILogger logger;

    @Override
    public Object[] handleArgs(@NotNull Object target, @NotNull Method method, @NotNull Object... args) {
        return args;
    }

    @Override
    public void handleException(@NotNull Throwable e, @NotNull Object target, @NotNull Method method,
        @NotNull Object... args) {

    }

    @Override
    public HandleReturnedResult handleReturn(@NotNull Object target, @NotNull Method method, Object result,
        @NotNull Object... args) {
        logger.info("Method: " + method.getName() + " returned: " + result);
        return HandleReturnedResult.finalResult(result);
    }
}
