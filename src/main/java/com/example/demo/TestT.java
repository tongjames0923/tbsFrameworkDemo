package com.example.demo;

import tbs.framework.auth.model.UserModel;

public class TestT implements ITest<UserModel> {
    @Override
    public void run(final UserModel userModel) {
        System.out.println(userModel.getUserId());
    }
}
