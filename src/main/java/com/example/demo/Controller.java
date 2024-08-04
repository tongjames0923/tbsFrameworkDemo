package com.example.demo;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;
import tbs.framework.auth.annotations.ApplyRuntimeData;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.base.interfaces.impls.chain.AbstractChain;
import tbs.framework.base.interfaces.impls.chain.AbstractCollectiveChain;
import tbs.framework.base.structs.ITree;
import tbs.framework.base.structs.impls.SimpleMultibranchTree;
import tbs.framework.base.structs.impls.TreeUtil;
import tbs.framework.cache.managers.AbstractExpireManager;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.mq.center.AbstractMessageCenter;
import tbs.framework.mq.consumer.manager.IMessageConsumerManager;
import tbs.framework.mq.message.impls.SimpleMessage;
import tbs.framework.proxy.impls.LockProxy;
import tbs.framework.sql.model.Page;
import tbs.framework.timer.AbstractTimer;
import tbs.framework.utils.ChainUtil;
import tbs.framework.utils.MultilingualUtil;
import tbs.framework.utils.ThreadUtil;
import tbs.framework.utils.UuidUtil;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author abstergo
 */
@RestController
public class Controller {

    @Resource
    LockProxy lockProxy;

    @Resource
    AsyncTest asyncTest;

    @Resource
    MultilingualUtil multilingualUtil;

    @AutoLogger
    public ILogger autoLogger;

    @Resource
    AbstractTimer timer;

    @Resource
    HomeMapper homeMapper;

    @Resource
    @Lazy
    AbstractMessageCenter messageCenter;

    @Resource
    RuntimeData runtimeData;

    @Resource
    AbstractExpireManager cacheService;

    @RequestMapping(value = "put", method = RequestMethod.GET)
    @ApplyRuntimeData
    public Result put(@RequestParam final String key, @RequestParam final String value, @RequestParam final long exp) {
        this.cacheService.put(key, value, true);
        if (0 < exp) {
            this.cacheService.expire(key, Duration.ofSeconds(exp));
        }
        return new Result("", 1, 0, null, null, null);
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    @ApplyRuntimeData
    public Result get(@RequestParam final String key) {
        return new Result("", 1, 0,
            Optional.ofNullable(this.cacheService.getAndRemove(key, Duration.ofSeconds(5))).orElse("null"), null, null);
    }

    //    @RequestMapping(value = "remain", method = RequestMethod.GET)
    //    @ApplyRuntimeData
    //    public Result remain(@RequestParam final String key) {
    //
    //        return new Result(String.valueOf(this.cacheService.remaining(key)), 0, 0, this.asyncTest.testModel(), null,
    //            null);
    //    }
    //
    //    @RequestMapping(value = "remove", method = RequestMethod.GET)
    //    @ApplyRuntimeData
    //    public Result remove(@RequestParam final String key) {
    //        this.cacheService.remove(key);
    //        return new Result("", 0, 0, this.asyncTest.testModel(), null, null);
    //    }

    @Resource
    LoginInfoMapper loginInfoMapper;

    @RequestMapping("testTransication")
    public TestModel testTransication(String text) {
        return asyncTest.testModel(text);
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

    int cnt = 0;

    @RequestMapping(value = "testWriteReadLock", method = RequestMethod.GET)
    public void testWriteReadLock() throws InterruptedException {
        cnt++;
        for (int i = 0; i < 1; i++) {
            ThreadUtil.getInstance().runCollectionInBackground(() -> {
                for (int j = 0; j < 100; j++) {
                    try {
                        asyncTest.testWrite();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        for (int i = 0; i < 1; i++) {
            ThreadUtil.getInstance().runCollectionInBackground(() -> {
                int oldv = -1;
                while (true) {
                    try {
                        int v = asyncTest.testRead();
                        //                        if (v != oldv) {
                        synchronized (this) {
                            oldv = v;
                            autoLogger.warn("Read!:{}", v);
                            //                            }
                        }

                        if (v >= cnt * 100) {
                            break;
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    @RequestMapping(value = "testCache", method = RequestMethod.GET)
    public String cacheTest(int id) throws Exception {
        String r = asyncTest.testCache(id);

        return StrUtil.isEmpty(r) ? "null" : r;
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

    @RequestMapping(value = "testLog", method = RequestMethod.GET)
    public String logTest(String value) throws Exception {
        autoLogger.info("this is a  logger for test {}", value);

        return "OK";
    }

    @RequestMapping(value = "testAsyncTask", method = RequestMethod.GET)
    public String lockTest() throws Exception {
        String uid = UuidUtil.getUuid();
        asyncTest.test1(uid);

        return uid;
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
                    synchronized (this) {
                        autoLogger.warn("pub {} at {}", l.get(), Thread.currentThread().getName());
                    }
                    messageCenter.publish(new SimpleMessage(topic, tag, null, random.nextInt(range)));
                    l.incrementAndGet();
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        return l.toString();
    }

    @Resource
    IMessageConsumerManager manager;

    @RequestMapping(value = "testCloseTopic", method = RequestMethod.POST)
    public String testTopicClose(String topic) throws Exception {
        //        List<IMessageReceiver> receivers = new LinkedList<>();
        //        for (IMessageReceiver messageReceiver : messageCenter.getReceivers()) {
        //            if (messageReceiver instanceof AbstractIdentityReceiver) {
        //                if (manager.match(topic, messageReceiver.acceptTopics())) {
        //                    receivers.add(messageReceiver);
        //                }
        //            }
        //        }
        return JSON.toJSONString("");
    }
    //
    //    @RequestMapping(value = "testOpenTopic", method = RequestMethod.POST)
    //    public String testTopicOpen(String topic) throws Exception {
    //        messageCenter.addReceivers(new RedisChannelReceiver(new IMessageConsumer() {
    //            @Override
    //            public String consumerId() {
    //                return topic;
    //            }
    //
    //            @Override
    //            public Set<String> avaliableTopics() {
    //                return new HashSet<>(Arrays.asList(topic));
    //            }
    //
    //            @Override
    //            public void consume(IMessage message) {
    //                autoLogger.info("i am a addable consumer");
    //            }
    //        }));
    //        return "ok";
    //    }

    @RequestMapping(value = "search", method = RequestMethod.POST)
    public List<LoginInfo> sysUserList(@RequestBody final LoginInfoQO qo, @RequestParam final int p,
        @RequestParam final int n) {
        return this.loginInfoMapper.queryByQO(qo, new Page(p, n));
    }

    @RequestMapping(value = "cmpTest", method = RequestMethod.GET)
    public String cmpTest() {
        ITree<Integer> root = new SimpleMultibranchTree<>();
        root.setValue(0);
        ITree<Integer> f1 = TreeUtil.appendNode(root, 3, TreeUtil::multiBranchTree);
        TreeUtil.appendNode(root, 5, TreeUtil::multiBranchTree);
        TreeUtil.appendNode(f1, 11, TreeUtil::multiBranchTree);
        TreeUtil.foreach(root, null, new TreeUtil.ITreeNodeForeach<Integer>() {

            @Override
            public void accept(ITree<Integer> patent, Integer v, int level) {
                for (int i = 0; i < level; i++) {
                    System.out.print("-");
                }
                System.out.printf("%d\n", v);
            }
        });
        return JSON.toJSONString(root);
    }

//    @Resource
//    CuratorFramework zooKeeper;

//    @RequestMapping(value = "zoo", method = RequestMethod.GET)
//    public String zoo(String path, int t, String text) throws Exception {
//        Object ob = null;
//        switch (t) {
//
//            case 1:
//                ob = zooKeeper.checkExists().forPath(path);
//                break;
//            case 2:
//                ob = new String(zooKeeper.getData().forPath(path));
//                break;
//            case 3:
//                ob = zooKeeper.setData().forPath(path, text.getBytes());
//                break;
//            case 4:
//                ob = zooKeeper.create().forPath(path, text.getBytes());
//                break;
//
//            default:
//                ob = "null";
//                break;
//        }
//        return JSON.toJSONString(ob);
//
//    }

    @GetMapping("testSql")
    public String insertLoigninfo() {
//        loginInfoMapper.testInsert(3, "f", "6", new LoginInfo());

                loginInfoMapper.insert(
                    new LoginInfo(256L, 10L, Integer.valueOf(1).byteValue(), null, Integer.valueOf(0).byteValue(), 10L, ""));
        return "ok";
    }

}
