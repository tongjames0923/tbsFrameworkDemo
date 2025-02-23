package com.example.demo;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import tbs.framework.auth.annotations.PermissionValidated;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author abstergo
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@PermissionValidated(userPermissionProvider = DbPermissionProvider.class)
public @interface DbPermission {
    @AliasFor(annotation = PermissionValidated.class, attribute = "value") String[] value() default {""};
}
