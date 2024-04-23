package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tbs.framework.auth.annotations.ApplyRuntimeData;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.lock.expections.ObtainLockFailException;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.proxy.IProxy;
import tbs.framework.base.proxy.impls.LockProxy;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.base.utils.MultilingualUtil;
import tbs.framework.timer.AbstractTimer;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

    public Controller(LogUtil util) {
        logger = util.getLogger(Controller.class.getName());
    }


    @Resource
    RuntimeData runtimeData;

    //    @Resource
    //    ITest<UserModel> userModelITest;
    //
    //    @Resource
    //    ITest<TestModel> testModelITest;

    @RequestMapping("/api/v1")
    @ApplyRuntimeData
    public Result index() throws InterruptedException, ExecutionException, ObtainLockFailException {
        //        userModelITest.run(runtimeData.getUserModel());
        //        testModelITest.run(new TestModel());
        Thread.currentThread().join(128);
        return new Result("11", 1, -1, new TestModel(), runtimeData.getUserModel(), null);
    }

    @RequestMapping("/a1/v1")
    @ApplyRuntimeData
    public Result index2() throws InterruptedException, ExecutionException, ObtainLockFailException {

        throw new RuntimeException("bad error");
//        return homeMapper.selectAll();
    }
}
