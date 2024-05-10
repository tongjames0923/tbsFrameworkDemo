package com.example.demo;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import tbs.framework.auth.interfaces.IErrorHandler;
import tbs.framework.auth.interfaces.IPermissionProvider;
import tbs.framework.auth.interfaces.IRuntimeDataExchanger;
import tbs.framework.auth.interfaces.IUserModelPicker;
import tbs.framework.auth.interfaces.impls.CopyRuntimeDataExchanger;
import tbs.framework.auth.model.RuntimeData;
import tbs.framework.auth.model.UserModel;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.mq.*;
import tbs.framework.mq.impls.QueueListener;
import tbs.framework.mq.impls.SimpleMessage;
import tbs.framework.mq.impls.SimpleMessageQueue;
import tbs.framework.redis.impls.AbstractRedisMessageCenter;
import tbs.framework.redis.impls.RedisMessageReceiver;
import tbs.framework.sql.interfaces.ISqlLogger;
import tbs.framework.sql.interfaces.impls.SimpleJsonLogger;
import tbs.framework.timer.AbstractTimer;
import tbs.framework.timer.impls.ScheduledExecutorTimer;
import tbs.framework.xxl.interfaces.IJsonJobHandler;
import tbs.framework.xxl.interfaces.IXXLJobsConfig;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Configuration
public class Config {

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
                return new HashSet<>(Arrays.asList("core"));
            }

            @Override
            public void consume(IMessage message) {
                if (logger == null) {
                    logger = LogUtil.getInstance().getLogger(this.getClass().getName() + ":" + this.consumerId());
                }
                logger.info("{} content:{}", message.getMessageId(), message.getTag());
                if (message instanceof SimpleMessage) {
                    SimpleMessage simpleMessage = (SimpleMessage)message;
                    simpleMessage.setConsumed();
                }
            }
        };
    }

    @Bean
    IMessageConsumer consumer2() {
        return new IMessageConsumer() {
            ILogger logger = null;



            @Override
            public String consumerId() {
                return "优先级测试";
            }

            @Override
            public Set<String> avaliableTopics() {
                return new HashSet<>(Arrays.asList("优先级"));
            }

            @Override
            public void consume(IMessage message) {
                if (logger == null) {
                    logger = LogUtil.getInstance().getLogger(this.getClass().getName() + ":" + this.consumerId());
                }
                logger.info("{} 优先级:{}", message.getMessageId(), message.getPriority());
                if (message instanceof SimpleMessage) {
                    SimpleMessage simpleMessage = (SimpleMessage)message;
                    simpleMessage.setConsumed();
                }
            }
        };
    }

    @Bean
    IMessageQueue messageQueue() {
        return new SimpleMessageQueue();
    }

    @Bean
    RedisMessageReceiver redisMessageReceiver(RedisMessageListenerContainer listenerContainer, IMessageQueue queue) {
        return new RedisMessageReceiver(listenerContainer, queue);
    }

    @Bean(destroyMethod = "centerStopToWork")
    IMessageQueueEvents abstractMessageCenter(RedisMessageReceiver receiver) {
        return new AbstractRedisMessageCenter(receiver) {
            private ILogger logger;

            private ILogger getLogger() {
                if (logger == null) {
                    logger = LogUtil.getInstance().getLogger(this.getClass().getName());
                }
                return logger;
            }

            @Override
            public void onMessageSent(IMessage message) {
                getLogger().info("onMessageSent: " + message);
            }



            @Override
            public boolean onMessageFailed(IMessage message, int retryed, MessageHandleType type, Throwable throwable,
                IMessageConsumer consumer) {
                getLogger().error(throwable, "onMessageFailed  retryed:{} type:{}", retryed, type);
                return false;
            }
        };

    }


    @Bean
    AbstractTimer timer(final LogUtil logUtil) {
        return new ScheduledExecutorTimer(
            Executors.newScheduledThreadPool(12, new CustomizableThreadFactory("timer-thread")), logUtil);
    }

    @Bean
    ISqlLogger sqlLogger(LogUtil logUtil) {
        return new SimpleJsonLogger(logUtil);
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
    IErrorHandler resultIErrorHandler(final LogUtil logUtil) {
        return new IErrorHandler() {
            private final ILogger logger = logUtil.getLogger("resultIErrorHandler");

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
