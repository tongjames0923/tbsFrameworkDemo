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
    public List<PermissionModel> retrievePermissions(final UserModel userModel, final String url, final Method method) {
        final ApiRight apiRight = new ApiRight();
        apiRight.setEnable(1);
        apiRight.setUrl(url);
        final List<ApiRight> apiRights = this.apiRightMapper.select(apiRight);
        final List<PermissionModel> permissionModels = new LinkedList<>();
        for (final ApiRight apiRight1 : apiRights) {
            final PermissionModel permissionModel = new PermissionModel();
            permissionModel.setUrl(apiRight1.getUrl());
            permissionModel.setRole(apiRight1.getId().toString());
            permissionModels.add(permissionModel);
        }
        return permissionModels;
    }
}
