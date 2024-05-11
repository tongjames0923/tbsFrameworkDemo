package com.example.demo;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.bind.annotation.*;
import tbs.framework.auth.annotations.ApplyRuntimeData;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.intefaces.impls.chain.AbstractChain;
import tbs.framework.base.intefaces.impls.chain.AbstractCollectiveChain;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.proxy.IProxy;
import tbs.framework.base.proxy.impls.LockProxy;
import tbs.framework.base.utils.*;
import tbs.framework.cache.ICacheService;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.message.impls.SimpleMessage;
import tbs.framework.sql.model.Page;
import tbs.framework.sql.utils.TransactionUtil;
import tbs.framework.timer.AbstractTimer;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class Controller {

    private final ILogger logger;

    @Resource
    LockProxy lockProxy;

    @Resource(name = BeanNameConstant.ERROR_LOG_PROXY)
    IProxy errorLogProxy;

    @Resource
    AsyncTest asyncTest;

    @Resource
    MultilingualUtil multilingualUtil;

    @Resource
    AbstractTimer timer;

    @Resource
    HomeMapper homeMapper;

    public Controller(final LogUtil util) {
        this.logger = util.getLogger(Controller.class.getName());
    }

    @Resource
    @Lazy
    AbstractMessageCenter messageCenter;


    @Resource
    RuntimeData runtimeData;

    @Resource
    ICacheService cacheService;

    @RequestMapping(value = "put", method = RequestMethod.GET)
    @ApplyRuntimeData
    public Result put(@RequestParam final String key, @RequestParam final String value, @RequestParam final long exp) {
        this.cacheService.put(key, value, false);
        if (0 < exp) {
            this.cacheService.expire(key, exp);
        }
        return new Result("", 1, 0, null, null, null);
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    @ApplyRuntimeData
    public Result get(@RequestParam final String key) {
        return new Result("", 1, 0, this.cacheService.get(key, true, 5).orElse("null"), null, null);
    }

    @RequestMapping(value = "remain", method = RequestMethod.GET)
    @ApplyRuntimeData
    public Result remain(@RequestParam final String key) {

        return new Result(String.valueOf(this.cacheService.remain(key)), 0, 0, this.asyncTest.testModel(), null, null);
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    @ApplyRuntimeData
    public Result remove(@RequestParam final String key) {
        this.cacheService.remove(key);
        return new Result("", 0, 0, this.asyncTest.testModel(), null, null);
    }

    @Resource
    LoginInfoMapper loginInfoMapper;

    @RequestMapping("testTransication")
    public String testTransication() {
        LoginInfo[] loginInfos = {
            new LoginInfo(null, 1L, Integer.valueOf(1).byteValue(), new Date(), Integer.valueOf(1).byteValue(), 1L,
                null),
            new LoginInfo(null, 2L, Integer.valueOf(1).byteValue(), new Date(), Integer.valueOf(1).byteValue(), 1L,
                null),
            new LoginInfo(null, 3L, Integer.valueOf(1).byteValue(), new Date(), Integer.valueOf(1).byteValue(), 1L,
                null)};
        TransactionUtil.getInstance().executeTransaction(Propagation.REQUIRED.value(),
            () -> loginInfoMapper.insertList(Arrays.asList(loginInfos)));
        return JSON.toJSONString(loginInfos);
    }

    static class RangeChain extends AbstractCollectiveChain<Void, Integer> {

        int i, mx;

        public RangeChain(int i, int max) {
            this.i = i;
            this.mx = max;
        }

        @Override
        public void doChain(Void param) {

            set(i);
            if (mx == i) {
                setAvailable(true);
            }
        }

    }

    AbstractCollectiveChain<Void, Integer> range(int from, int to) {
        AbstractChain.Builder<Void, Integer, AbstractCollectiveChain<Void, Integer>> builder = AbstractChain.newChain();
        for (int i = from; i < to; i++) {
            builder.add(new RangeChain(i, to - 1));
        }
        return builder.build();
    }

    @RequestMapping(value = "testChain", method = RequestMethod.GET)
    public List<Integer> test() {
        return ((AbstractCollectiveChain<Void, Integer>)ChainUtil.processForChain(range(0, 100),
            null)).collectFromChain();
    }

    @RequestMapping(value = "testCache", method = RequestMethod.GET)
    public String cacheTest(int id) throws Exception {
        String r= asyncTest.testCache(id);

        return StrUtil.isEmpty(r)?"null":r;
    }

    @Resource
    ThreadUtil threadUtil;

    @RequestMapping(value = "uuidTest", method = RequestMethod.GET)
    public List<String> uuidTest(int t, int per) throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(t);
        String[][] arr = new String[t][per];
        List<Runnable> callables = new LinkedList<>();
        for (int i = 0; i < t; i++) {
            int finalI = i;
            callables.add(() -> {
                for (int j = 0; j < per; j++) {
                    arr[finalI][j] = UuidUtil.getUuid();
                }
                countDownLatch.countDown();
                if (finalI == 3) {
                    throw new RuntimeException("just error");
                }
            });
        }
        threadUtil.runCollectionInBackground(callables);
        countDownLatch.await();
        List<String> list = new LinkedList<>();
        for (int i = 0; i < t; i++) {
            for (int j = 0; j < per; j++) {
                list.add(arr[i][j]);
            }
        }

        return list;
    }

    @RequestMapping(value = "unique", method = RequestMethod.POST)
    public Boolean isUnique(@RequestBody final List<String> value) {
        Set<String> h = new HashSet<>();
        for (int i = 0; i < value.size(); i++) {
            if (h.contains(value.get(i))) {
                return false;
            }
            h.add(value.get(i));
        }
        return true;
    }

    @RequestMapping(value = "testLock", method = RequestMethod.GET)
    public String lockTest() throws Exception {
        asyncTest.test1();

        return asyncTest.test().get();
    }

    @Data
    public static class MessageParam implements Serializable {
        private static final long serialVersionUID = 197488360569395600L;
        String tag;
        String topic;
        int priorty;
        Map<String, Object> headers;

    }

    @RequestMapping(value = "testMsgCenter", method = RequestMethod.POST)
    public String testMsgCenter(@RequestBody MessageParam body) throws Exception {
        messageCenter.publish(new SimpleMessage(body.topic, body.tag, body.headers, body.priorty));

        return "";
    }

    Random random = new Random();

    @RequestMapping(value = "testMsgCenterP", method = RequestMethod.POST)
    public String testMsgCenterP(int t, int n, int range, String tag, String topic) throws Exception {
        AtomicLong l = new AtomicLong(0);
        CountDownLatch countDownLatch = new CountDownLatch(t);
        for (int i = 0; i < t; i++) {
            threadUtil.runCollectionInBackground(() -> {
                for (int j = 0; j < n / t; j++) {
                    messageCenter.publish(new SimpleMessage(topic, tag, null, random.nextInt(range)));
                    l.incrementAndGet();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        return l.toString();
    }

    @RequestMapping(value = "search", method = RequestMethod.POST)
    public List<LoginInfo> sysUserList(@RequestBody final LoginInfoQO qo, @RequestParam final int p, @RequestParam final int n) {
        return this.loginInfoMapper.queryByQO(qo, new Page(p, n));
    }
}
