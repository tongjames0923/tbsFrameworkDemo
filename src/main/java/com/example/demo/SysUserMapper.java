package com.example.demo;

import org.apache.ibatis.annotations.Mapper;
import tbs.framework.sql.interfaces.mappers.QueryMapper;
import tk.mybatis.mapper.common.BaseMapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser>, QueryMapper<SysUser> {
}
