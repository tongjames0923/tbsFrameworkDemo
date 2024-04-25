package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.base.constants.BeanNameConstant;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.proxy.IProxy;
import tbs.framework.base.proxy.impls.LockProxy;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.base.utils.MultilingualUtil;
import tbs.framework.cache.ICacheService;
import tbs.framework.timer.AbstractTimer;

import javax.annotation.Resource;

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

    @Resource
    ICacheService cacheService;

    @RequestMapping(value = "put", method = RequestMethod.GET)
    public Result put(@RequestParam String key, @RequestParam String value, @RequestParam long exp) {
        cacheService.put(key, value, false);
        if (0 < exp) {
            cacheService.expire(key, exp);
        }
        return new Result("", 1, 0, null, null, null);
    }

    @RequestMapping(value = "get", method = RequestMethod.GET)
    public Result get(@RequestParam String key) {
        return new Result("", 1, 0, cacheService.get(key, true, 5).orElse("null"), null, null);
    }

    @RequestMapping(value = "remain", method = RequestMethod.GET)
    public Result remain(@RequestParam String key) {

        return new Result(String.valueOf(cacheService.remain(key)), 0, 0, asyncTest.testModel(), null, null);
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public Result remove(@RequestParam String key) {
        cacheService.remove(key);
        return new Result("", 0, 0, asyncTest.testModel(), null, null);
    }

}
