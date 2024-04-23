package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import tbs.framework.auth.model.UserModel;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Result implements Serializable {
    private static final long serialVersionUID = 1L;
    private String message;
    private int code;
    private long cost;
    private Object data;
    private UserModel userModel;
    private String invokeUrl;
}
