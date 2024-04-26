package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tbs.framework.auth.annotations.PermissionValidated;

/**
 * @author Abstergo
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @DbPermission
    @PermissionValidated(value = "70")
    @PermissionValidated(value = "66")
    @RequestMapping("a")
    public String a() {

        return "Hello World a";
    }

    @RequestMapping("b")
    @PermissionValidated("PASS")
    public String b() {
        return "Hello World b";
    }

    @RequestMapping("c")
    @PermissionValidated("NOT PASS")
    public String c() {
        return "Hello World c";
    }

}
