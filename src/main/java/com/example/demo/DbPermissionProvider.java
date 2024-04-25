package com.example.demo;

import tbs.framework.auth.interfaces.IPermissionProvider;
import tbs.framework.auth.model.PermissionModel;
import tbs.framework.auth.model.UserModel;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class DbPermissionProvider implements IPermissionProvider {

    @Resource
    ApiRightMapper apiRightMapper;

    @Override
    public List<PermissionModel> retrievePermissions(UserModel userModel, String url, Method method) {
        ApiRight apiRight = new ApiRight();
        apiRight.setEnable(1);
        apiRight.setUrl(url);
        List<ApiRight> apiRights = apiRightMapper.select(apiRight);
        List<PermissionModel> permissionModels = new LinkedList<>();
        for (ApiRight apiRight1 : apiRights) {
            PermissionModel permissionModel = new PermissionModel();
            permissionModel.setUrl(apiRight1.getUrl());
            permissionModel.setRole(apiRight1.getId().toString());
            permissionModels.add(permissionModel);
        }
        return permissionModels;
    }
}
