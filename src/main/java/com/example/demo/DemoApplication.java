package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tbs.framework.base.annotations.EnableMultilingual;
import tbs.framework.base.annotations.EnableTbsAsync;
import tbs.framework.base.annotations.EnableTbsFramework;
import tbs.framework.xxl.annotations.EnableTbsXXL;

@EnableTbsFramework
@SpringBootApplication
@EnableTbsAsync
//@EnableTbsXXL
@EnableMultilingual
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
