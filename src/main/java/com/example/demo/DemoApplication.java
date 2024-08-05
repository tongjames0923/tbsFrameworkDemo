package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tbs.framework.auth.annotations.EnableTbsAuth;
import tbs.framework.auth.annotations.UseDebounce;
import tbs.framework.base.annotations.EnableMultilingual;
import tbs.framework.base.annotations.EnableTbsAsync;
import tbs.framework.base.annotations.EnableTbsCache;
import tbs.framework.redis.annotations.EnableTbsRedis;
import tbs.framework.sql.annotations.EnableTbsSqlUtils;
import tbs.framework.swagger.annotations.EnableTbsSwagger;

/**
 * @author abstergo
 */
@EnableTbsRedis
@EnableTbsAuth
@SpringBootApplication
@EnableTbsAsync
@EnableMultilingual
@EnableTbsCache
@EnableTbsSwagger
@EnableTbsSqlUtils
//@EnableRabbitMqQueue
//@EnableZooKeeper

@UseDebounce
public class DemoApplication {

    public static void main(final String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
