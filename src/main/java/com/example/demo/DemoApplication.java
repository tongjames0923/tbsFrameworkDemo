package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tbs.framework.auth.annotations.EnableTbsAuth;
import tbs.framework.base.annotations.EnableMultilingual;
import tbs.framework.base.annotations.EnableTbsAsync;
import tbs.framework.base.annotations.EnableTbsCache;
import tbs.framework.base.annotations.EnableTbsFramework;
import tbs.framework.swagger.annotations.EnableTbsSwagger;
import tbs.framework.xxl.annotations.EnableTbsXXL;

@EnableTbsFramework
@EnableTbsAuth
@SpringBootApplication
@EnableTbsAsync
//@EnableTbsXXL
@EnableMultilingual
@EnableTbsCache
@EnableTbsSwagger
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
