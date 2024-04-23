package com.example.demo;

import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.BaseMapper;

@Mapper
public interface HomeMapper extends BaseMapper<HomeEntity> {
}
