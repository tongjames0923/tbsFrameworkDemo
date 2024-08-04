package com.example.demo;

import lombok.Data;
import tbs.framework.multilingual.annotations.TranslateField;
import tbs.framework.multilingual.impls.parameters.TimeParameter;

@Data
public class TestModel {
    @TranslateField(args = TimeParameter.class)
    private String text = "TIME.NOW";

    public TestModel(String text) {
        this.text = text;
    }
}
