package com.example.demo;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.cache.annotations.CacheLoading;
import tbs.framework.cache.annotations.CacheUnloading;
import tbs.framework.lock.IReadWriteLock;
import tbs.framework.lock.expections.ObtainLockFailException;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.multilingual.annotations.Translated;
import tbs.framework.proxy.impls.LockProxy;
import tbs.framework.utils.ThreadUtil;

import javax.annotation.Resource;
import java.time.Duration;
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

    private static final String READ = "read", WRITE = "write";

    @Resource
    IReadWriteLock simpleReadWriteLock;

    int r = 0;

    public int testRead() throws InterruptedException {

        logger.info("reading ...");
        int v = -1;
        simpleReadWriteLock.readLock().tryLock(Duration.ofMinutes(1));
        //        read.lock();
        v = r;
        //        read.unlock();
        simpleReadWriteLock.readLock().unLock();
        logger.info("read end");
        return v;
    }

    public void testWrite() throws InterruptedException {
        logger.info("writing ...");
        simpleReadWriteLock.writeLock().tryLock(Duration.ofMinutes(1));
        //        write.lock();
        r++;
        //        write.unlock();
        simpleReadWriteLock.writeLock().unLock();
        logger.info("write end");
    }

    @CacheUnloading(key = "#method.#args[0]", intArgs = 20000)
    @CacheLoading(key = "#method.#args[0]")
    public String testCache(int id) throws InterruptedException {
        Thread.currentThread().join(1000);
        return "hello world";
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

    public String test1(String id) throws ObtainLockFailException {
        Integer[] v = new Integer[] {0};
        for (int i = 0; i < 5; i++) {
            ThreadUtil.getInstance().runCollectionInBackground(() -> {
                for (int j = 0; j < 200; j++) {
                    LockProxy.getInstance().quickLock(() -> {
                        logger.info("value :{}", v[0]++);
                    }, ThreadUtil.getInstance().getLock(id));
                }
            });
        }

        return null;

    }

    @Translated
    public TestModel testModel() {
        return new TestModel();
    }
}
