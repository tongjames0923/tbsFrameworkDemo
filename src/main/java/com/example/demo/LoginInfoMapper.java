package com.example.demo;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import tbs.framework.sql.interfaces.mappers.IFrameworkMapper;

@Mapper
public interface LoginInfoMapper extends IFrameworkMapper<LoginInfo, Long> {

    @Delete("delete from login_info")
    public int testInsert(long id, String name, String password, LoginInfo info);
}
