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
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.timer.AbstractTimer;
import tbs.framework.timer.impls.ScheduledExecutorTimer;
import tbs.framework.xxl.interfaces.IJsonJobHandler;
import tbs.framework.xxl.interfaces.IXXLJobsConfig;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Configuration
public class Config {

    @Bean
    AbstractTimer timer(LogUtil logUtil) {
        return new ScheduledExecutorTimer(
            Executors.newScheduledThreadPool(12, new CustomizableThreadFactory("timer-thread")), logUtil);
    }

    private static class ResultExchanger extends CopyRuntimeDataExchanger<Result> {
        @Override
        public Result exchange(RuntimeData data, Result val) {
            try {
                val.setCost(Duration.between(data.getInvokeBegin(), data.getInvokeEnd()).toMillis());
            } catch (Exception e) {
            }
            return super.exchange(data, val);
        }
    }

    @Bean
    IRuntimeDataExchanger<Result> runtimeDataExchanger() {
        return new ResultExchanger();
    }

    @Bean
    IErrorHandler resultIErrorHandler(LogUtil logUtil) {
        return new IErrorHandler() {
            private ILogger logger = logUtil.getLogger("resultIErrorHandler");

            @Override
            public Object handleError(Throwable ex) {
                logger.error(ex, ex.getMessage());
                    return new Result(ex.getMessage(), -300, -1, null, null, null);
            }
        };
    }
    @Bean
    IUserModelPicker userModelPicker(UserRightMapper userRightMapper, SysUserMapper sysUserMapper) {
        return new IUserModelPicker() {
            @Override
            public UserModel getUserModel(String token) {
                SysUser user = sysUserMapper.selectByPrimaryKey(token);
                if (user != null) {
                    UserModel model = new UserModel();
                    UserRight right = new UserRight();
                    right.setDeleteMark(0);
                    right.setUserId(user.getId());
                    List<UserRight> ls = userRightMapper.select(right);
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
            public Class<? extends String> classType() {
                return String.class;
            }

            @Override
            public String paramConvert(Map mp) {
                if (mp.isEmpty()) {
                    return "";
                }
                return new LinkedList(mp.values()).get(0).toString();
            }

            @Override
            public String handle(String params) throws Exception {
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
