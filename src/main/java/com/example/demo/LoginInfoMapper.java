package com.example.demo;

import org.apache.ibatis.annotations.Mapper;
import tbs.framework.sql.interfaces.mappers.IFrameworkMapper;

@Mapper
public interface LoginInfoMapper extends IFrameworkMapper<LoginInfo, Long> {
}
