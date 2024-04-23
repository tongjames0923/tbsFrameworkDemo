package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @RequestMapping("/")
    public List<HomeEntity> index() throws InterruptedException, ExecutionException, ObtainLockFailException {

        return homeMapper.selectAll();
    }
}
