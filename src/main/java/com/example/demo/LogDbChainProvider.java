package com.example.demo;

import com.alibaba.fastjson2.JSON;
import tbs.framework.base.interfaces.impls.chain.AbstractChain;
import tbs.framework.log.AbstractLogChainProvider;
import tbs.framework.log.ILogger;
import tbs.framework.log.impls.ChainLogger;
import tbs.framework.log.impls.Slf4jLogger;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Abstergo
 */
public class LogDbChainProvider extends AbstractLogChainProvider {

    @Resource
    LogDataMapper logDataMapper;

    @Override
    public AbstractChain<ChainLogger.LogArg, Void> beginChain() {
        return new AbstractChain.Builder<ChainLogger.LogArg, Void, AbstractChain<ChainLogger.LogArg, Void>>().add(
            new AbstractChain<ChainLogger.LogArg, Void>() {

                private Map<String, ILogger> loggerMap = new HashMap<>();

                @Override
                public void doChain(ChainLogger.LogArg param) {
                    ILogger logger =
                        loggerMap.getOrDefault(param.getLoggerName(), new Slf4jLogger(param.getLoggerName()));
                    switch (param.getLevel()) {
                        case INFO:
                            logger.info(param.getMessage(), param.getArgs());
                            break;
                        case WARN:
                            logger.warn(param.getMessage(), param.getArgs());
                            break;
                        case ERROR:
                            logger.error(null, param.getMessage(), param.getArgs());
                            break;
                        case DEBUG:
                            logger.debug(param.getMessage(), param.getArgs());
                            break;
                        case TRACE:
                            logger.trace(param.getMessage(), param.getArgs());
                            break;
                        default:
                            break;
                    }
                    if (!loggerMap.containsKey(param.getLoggerName())) {
                        loggerMap.put(param.getLoggerName(), logger);
                    }
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
