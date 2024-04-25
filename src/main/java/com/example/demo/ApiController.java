package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tbs.framework.auth.annotations.PermissionValidated;

@RestController
@RequestMapping("/api")
public class ApiController {

    @RequestMapping(value = "a")
    @PermissionValidated(value = "PASS")
    public String a() {

        return "Hello World a";
    }

    @RequestMapping(value = "b")
    @PermissionValidated(value = "PASS")
    public String b() {
        return "Hello World b";
    }

    @RequestMapping(value = "c")
    @PermissionValidated(value = "NOT PASS")
    public String c() {
        return "Hello World c";
    }

}
