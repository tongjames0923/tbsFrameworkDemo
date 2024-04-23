package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.timer.AbstractTimer;
import tbs.framework.timer.impls.ScheduledExecutorTimer;
import tbs.framework.xxl.interfaces.IJsonJobHandler;
import tbs.framework.xxl.interfaces.IXXLJobsConfig;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executors;

@Configuration
public class Config {

    @Bean
    AbstractTimer timer(LogUtil logUtil) {
        return new ScheduledExecutorTimer(
            Executors.newScheduledThreadPool(12, new CustomizableThreadFactory("timer-thread")), logUtil);
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
