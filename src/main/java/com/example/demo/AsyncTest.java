package com.example.demo;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.lock.annotations.LockIt;
import tbs.framework.base.lock.expections.ObtainLockFailException;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.proxy.impls.LockProxy;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.cache.annotations.CacheLoading;
import tbs.framework.cache.annotations.CacheUnloading;
import tbs.framework.cache.impls.broker.NullableCacheBroker;
import tbs.framework.cache.impls.eliminate.ExpireCacheStrategy;
import tbs.framework.multilingual.annotations.Translated;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Component
public class AsyncTest {

    private final ILogger logger;

    @Resource
    LockProxy lockProxy;

    public AsyncTest(final LogUtil logUtil) {
        logger = logUtil.getLogger(AsyncTest.class.getName());
    }

    @CacheUnloading(key = "#args[0]", eliminationStrategy = ExpireCacheStrategy.class, intArgs = {60})
    @CacheLoading(key = "#args[0]",cacheBroker = NullableCacheBroker.class)
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

    @Async(BeanNameConstant.ASYNC_EXECUTOR)
    @LockIt(lockId = "h")
    public void test1() throws ObtainLockFailException {
        try {
            Thread.currentThread().join(1000);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.logger.info("Hello World!");
    }

    @Translated
    public TestModel testModel() {
        return new TestModel();
    }
}
