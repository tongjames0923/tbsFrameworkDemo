package com.example.demo;

import com.alibaba.fastjson2.JSON;
import tbs.framework.base.intefaces.IChainProvider;
import tbs.framework.base.intefaces.impls.chain.AbstractChain;
import tbs.framework.log.ILogger;
import tbs.framework.log.impls.ChainLogger;
import tbs.framework.log.impls.Slf4jLogger;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author Abstergo
 */
public class LogDbChainProvider implements IChainProvider<ChainLogger.LogArg, Void> {

    @Resource
    LogDataMapper logDataMapper;

    @Override
    public AbstractChain<ChainLogger.LogArg, Void> beginChain() {
        return new AbstractChain.Builder<ChainLogger.LogArg, Void, AbstractChain<ChainLogger.LogArg, Void>>().add(
            new AbstractChain<ChainLogger.LogArg, Void>() {
                @Override
                public void doChain(ChainLogger.LogArg param) {
                    ILogger logger = new Slf4jLogger(param.getLoggerName());
                    switch (param.getLevel()) {
                        case INFO:
                            logger.info(param.getMessage());
                            break;
                        case WARN:
                            logger.warn(param.getMessage());
                            break;
                        case ERROR:
                            logger.error(null, param.getMessage());
                            break;
                        case DEBUG:
                            logger.debug(param.getMessage());
                            break;
                        case TRACE:
                            logger.trace(param.getMessage());
                            break;
                        default:
                            break;
                    }
                    logger = null;
                }
            }).add(new AbstractChain<ChainLogger.LogArg, Void>() {
            @Override
            public void doChain(ChainLogger.LogArg param) {

                logDataMapper.insert(new LogDataEntity(null, new Date(),
                    param.getMessage() + "args:[" + JSON.toJSONString(param.getArgs()) + "]", param.getLoggerName(),
                    param.getLevel().name()));

            }
        }).build();
    }
}
