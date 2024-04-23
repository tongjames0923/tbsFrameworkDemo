package com.example.demo;

import lombok.Data;
import tbs.framework.multilingual.annotations.TranslateField;

@Data
public class TestModel {
    @TranslateField
    private String text = "HELLO.WORLD";
}
