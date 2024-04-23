package com.example.demo;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.lock.annotations.LockIt;
import tbs.framework.base.lock.expections.ObtainLockFailException;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.proxy.impls.LockProxy;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.multilingual.annotations.Translated;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Component
public class AsyncTest {

    private final ILogger logger;

    @Resource
    LockProxy lockProxy;

    public AsyncTest(LogUtil logUtil) {
        this.logger = logUtil.getLogger(AsyncTest.class.getName());
    }

    @Async(BeanNameConstant.ASYNC_EXECUTOR)
    public Future<String> test() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return lockProxy.proxy((a) -> {
                    logger.info("start wait");
                    try {
                        Thread.currentThread().join(12000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    logger.info("end onece");
                    return "Hello World!";
                }, null).orElse("error got");
            } catch (ObtainLockFailException e) {
                throw new RuntimeException(e);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        });
    }

    @Async(value = BeanNameConstant.ASYNC_EXECUTOR)
    @LockIt
    public void test1() throws ObtainLockFailException {
        try {
            Thread.currentThread().join(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("Hello World!");
    }

    @Translated
    public TestModel testModel() {
        return new TestModel();
    }
}
