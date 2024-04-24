package com.example.demo;

import lombok.Data;
import tbs.framework.multilingual.annotations.TranslateField;
import tbs.framework.multilingual.impls.parameters.CustomParameter;

@Data
public class TestModel {
    @TranslateField(args = CustomParameter.class)
    private String text = "TIME.NOW";
}
