package com.example.demo

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tbs.framework.auth.annotations.Debounce

/**
 * @author Abstergo
 */
@RestController
@RequestMapping("/api")
class ApiController {
    @DbPermission("70", "66")
    @GetMapping("a")
    @Debounce
    fun a(): String {
        return "Hello World a"
    }

    @DbPermission("70", "66")
    @GetMapping("b")
    @Debounce
    fun b(p: Int): String {
        return "Hello World a" + p;
    }


//    @Resource
//    lateinit var aesTokenDebounce: AESTokenDebounce;
//
//    @GetMapping("getDebounce")
//    fun getDebounce(url: String): String {
//        return aesTokenDebounce.generateToken(
//            AESTokenDebounce.DebounceInfo(
//                RuntimeData.getInstance().userModel.userId,
//                url
//            )
//        );
//    }

}
