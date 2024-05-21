package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import tbs.framework.auth.interfaces.IErrorHandler;
import tbs.framework.auth.interfaces.IPermissionProvider;
import tbs.framework.auth.interfaces.IRuntimeDataExchanger;
import tbs.framework.auth.interfaces.IUserModelPicker;
import tbs.framework.auth.interfaces.impls.CopyRuntimeDataExchanger;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.auth.model.UserModel;
import tbs.framework.log.AbstractLogChainProvider;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.log.impls.Slf4jLogger;
import tbs.framework.mq.consumer.IMessageConsumer;
import tbs.framework.mq.message.IMessage;
import tbs.framework.sql.interfaces.ISqlLogger;
import tbs.framework.sql.interfaces.impls.SimpleJsonLogger;
import tbs.framework.timer.AbstractTimer;
import tbs.framework.timer.impls.ScheduledExecutorTimer;
import tbs.framework.utils.LogUtil;
import tbs.framework.utils.impls.ChainLoggerUtil;
import tbs.framework.xxl.interfaces.IJsonJobHandler;
import tbs.framework.xxl.interfaces.IXXLJobsConfig;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author abstergo
 */
@Configuration
public class Config {

    @Bean(ChainLoggerUtil.LOGGER_CHAIN)
    AbstractLogChainProvider logChain() {
        return new LogDbChainProvider();
    }

    //    @Bean
    //    AbstractMessageCenter center() {
    //        return new MessageQueueCenter();
    //    }

    @Bean
    IMessageConsumer consumer1() {
        return new IMessageConsumer() {

            ILogger logger = null;

            @Override
            public String consumerId() {
                return "logger";
            }

            @Override
            public Set<String> avaliableTopics() {
                return new HashSet<>(Arrays.asList(".*"));
            }

            @Override
            public void consume(IMessage message) {
                if (logger == null) {
                    logger = LogUtil.getInstance().getLogger(this.getClass().getName() + ":" + this.consumerId());
                }
                logger.info("{} content:{}", message.getMessageId(), message.getTag());

            }
        };
    }

    @Bean
    IMessageConsumer consumer2() {
        return new IMessageConsumer() {
            @AutoLogger
            ILogger logger = null;

            @Override
            public String consumerId() {
                return "优先级测试";
            }

            @Override
            public Set<String> avaliableTopics() {
                return new HashSet<>(Arrays.asList("优先级.*"));
            }

            @Override
            public void consume(IMessage message) {
                if (logger == null) {
                    logger = LogUtil.getInstance().getLogger(this.getClass().getName() + ":" + this.consumerId());
                }
                logger.info("{} 优先级:{}", message.getMessageId(), message.getPriority());

            }
        };
    }

    @Bean
    AbstractTimer timer() {
        return new ScheduledExecutorTimer(
            Executors.newScheduledThreadPool(12, new CustomizableThreadFactory("timer-thread")));
    }

    @Bean
    ISqlLogger sqlLogger() {
        return new SimpleJsonLogger(new Slf4jLogger(SimpleJsonLogger.class.getName()));
    }

    private static class ResultExchanger extends CopyRuntimeDataExchanger<Result> {
        @Override
        public Result exchange(final RuntimeData data, final Result val) {
            try {
                val.setCost(Duration.between(data.getInvokeBegin(), data.getInvokeEnd()).toMillis());
            } catch (final Exception e) {
            }
            return super.exchange(data, val);
        }
    }

    @Bean
    IRuntimeDataExchanger<Result> runtimeDataExchanger() {
        return new ResultExchanger();
    }

    @Bean
    IErrorHandler resultIErrorHandler() {
        return new IErrorHandler() {
            @AutoLogger
            private ILogger logger;

            @Override
            public Object handleError(final Throwable ex) {
                this.logger.error(ex, ex.getMessage());
                return new Result(ex.getMessage(), -300, -1, ex, null, RuntimeData.getInstance().getInvokeUrl());
            }
        };
    }

    @Resource
    ApiRightMapper apiRightMapper;

    @Bean
    IUserModelPicker userModelPicker(final UserRightMapper userRightMapper, final SysUserMapper sysUserMapper) {
        return new IUserModelPicker() {
            @Override
            public UserModel getUserModel(final String token) {
                final SysUser user = sysUserMapper.selectByPrimaryKey(token);
                if (null != user) {
                    final UserModel model = new UserModel();
                    final UserRight right = new UserRight();
                    right.setDeleteMark(0);
                    right.setUserId(user.getId());
                    final ApiRight apiRight = new ApiRight();
                    apiRight.setEnable(1);
                    apiRight.setUrl(RuntimeData.getInstance().getInvokeUrl());
                    final Set<Long> apiIds = Config.this.apiRightMapper.select(apiRight).stream().map((a) -> {
                        return Optional.ofNullable(a).map(ApiRight::getId).orElse(-1L);
                    }).collect(Collectors.toSet());
                    final List<UserRight> ls = userRightMapper.select(right).stream().filter((t) -> {
                        return apiIds.contains(t.getRightsId());
                    }).collect(Collectors.toList());
                    model.setUserRole(ls.stream().map((ur) -> {
                        return String.format("%s", ur.getRightsId());
                    }).collect(Collectors.toSet()));
                    model.setUserId(user.getId().toString());
                    return model;
                } else {
                    return null;
                }

            }
        };
    }

    @Bean
    IPermissionProvider permissionProvider() {
        return new DbPermissionProvider();
    }

    @Bean
    public tbs.framework.xxl.interfaces.IJsonJobHandler<String> tester() {
        return new IJsonJobHandler<String>() {
            @Override
            public Class<String> classType() {
                return String.class;
            }

            @Override
            public String paramConvert(final Map mp) {
                if (mp.isEmpty()) {
                    return "";
                }
                return new LinkedList(mp.values()).get(0).toString();
            }

            @Override
            public String handle(final String params) throws Exception {
                return String.format("Hello %s!", params);
            }
        };
    }

    @Bean
    public IXXLJobsConfig xxl() {
        return new IXXLJobsConfig() {
            @Override
            public String adminAddress() {
                return "http://127.0.0.1:8899/xxl-job-admin";
            }

            @Override
            public String appName() {
                return "demoApplication";
            }

            @Override
            public String address() {
                return null;
            }

            @Override
            public String ip() {
                return null;
            }

            @Override
            public Integer port() {
                return 0;
            }

            @Override
            public String accessToken() {
                return "default_token";
            }

            @Override
            public String logPath() {
                return "/Users/abstergo/Downloads/log/xxl-job.log";
            }

            @Override
            public Integer logRetentionsDays() {
                return 1;
            }
        };
    }
}
