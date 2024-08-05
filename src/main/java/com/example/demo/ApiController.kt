package com.example.demo

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tbs.framework.auth.annotations.Debounce
import tbs.framework.auth.annotations.PermissionValidated

/**
 * @author Abstergo
 */
@RestController
@RequestMapping("/api")
class ApiController {
    @DbPermission
    @PermissionValidated("70", "66")
    @GetMapping("a")
    @Debounce
    fun a(): String {
        return "Hello World a"
    }

//    @Resource
//    lateinit var aesTokenDebounce: AESTokenDebounce;
//
//    @GetMapping("getDebounce")
//    fun getDebounce(url: String): String {
//        return aesTokenDebounce.makeToken(RuntimeData.getInstance().userModel, url);
//    }

    @RequestMapping("b")
    @PermissionValidated("PASS")
    fun b(): String {
        return "Hello World b"
    }

    @RequestMapping("c")
    @PermissionValidated("NOT PASS")
    fun c(): String {
        return "Hello World c"
    }
}
