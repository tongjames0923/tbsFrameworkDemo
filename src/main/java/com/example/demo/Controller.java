package com.example.demo;

import com.alibaba.fastjson2.JSON;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.bind.annotation.*;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.intefaces.impls.chain.AbstractChain;
import tbs.framework.base.intefaces.impls.chain.AbstractCollectiveChain;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.proxy.IProxy;
import tbs.framework.base.proxy.impls.LockProxy;
import tbs.framework.base.utils.ChainUtil;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.base.utils.MultilingualUtil;
import tbs.framework.cache.ICacheService;
import tbs.framework.sql.model.Page;
import tbs.framework.sql.utils.TransactionUtil;
import tbs.framework.timer.AbstractTimer;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    RuntimeData runtimeData;

    @Resource
    ICacheService cacheService;

    @RequestMapping(value = "put", method = RequestMethod.GET)
    public Result put(@RequestParam final String key, @RequestParam final String value, @RequestParam final long exp) {
        this.cacheService.put(key, value, false);
        if (0 < exp) {
            this.cacheService.expire(key, exp);
        }
        return new Result("", 1, 0, null, null, null);
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    public Result get(@RequestParam final String key) {
        return new Result("", 1, 0, this.cacheService.get(key, true, 5).orElse("null"), null, null);
    }

    @RequestMapping(value = "remain", method = RequestMethod.GET)
    public Result remain(@RequestParam final String key) {

        return new Result(String.valueOf(this.cacheService.remain(key)), 0, 0, this.asyncTest.testModel(), null, null);
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
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

    @RequestMapping(value = "testLock", method = RequestMethod.GET)
    public String lockTest() throws Exception {
        asyncTest.test1();

        return asyncTest.test().get();
    }

    @RequestMapping(value = "search", method = RequestMethod.POST)
    public List<LoginInfo> sysUserList(@RequestBody final LoginInfoQO qo, @RequestParam final int p, @RequestParam final int n) {
        return this.loginInfoMapper.queryByQO(qo, new Page(p, n));
    }
}
