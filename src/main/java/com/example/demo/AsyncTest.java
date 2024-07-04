package com.example.demo;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tbs.framework.async.task.annotations.AsyncTaskId;
import tbs.framework.async.task.annotations.AsyncWithCallback;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.cache.annotations.CacheLoading;
import tbs.framework.cache.annotations.CacheUnloading;
import tbs.framework.cache.impls.broker.NullableCacheBroker;
import tbs.framework.cache.impls.eliminate.ExpireCacheStrategy;
import tbs.framework.lock.annotations.LockIt;
import tbs.framework.lock.expections.ObtainLockFailException;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.multilingual.annotations.Translated;
import tbs.framework.proxy.impls.LockProxy;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Component
public class AsyncTest {

    @AutoLogger
    private ILogger logger;

    @Resource
    LockProxy lockProxy;

    public AsyncTest() {

    }

    @CacheUnloading(key = "#args[0]", eliminationStrategy = ExpireCacheStrategy.class, intArgs = {60})
    @CacheLoading(key = "#args[0]", cacheBroker = NullableCacheBroker.class)
    public String testCache(int id) throws InterruptedException {
        Thread.currentThread().join(1000);
        return null;
    }

    @Async(BeanNameConstant.ASYNC_EXECUTOR)
    public Future<String> test() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.lockProxy.proxy((a) -> {
                    this.logger.info("start wait");
                    try {
                        Thread.currentThread().join(1000);
                    } catch (final InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    this.logger.info("end onece");
                    return "Hello World!";
                }, null, null).orElse("error got");
            } catch (final ObtainLockFailException e) {
                throw new RuntimeException(e);
            } catch (final Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        });
    }

    @AsyncWithCallback
    @LockIt(lockId = "h")
    public String test1(@AsyncTaskId String id) throws ObtainLockFailException {
        try {
            Thread.currentThread().join(5000);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.logger.info("Hello World!");
        return "Hello World!~~~~";
    }

    @Translated
    public TestModel testModel() {
        return new TestModel();
    }
}
