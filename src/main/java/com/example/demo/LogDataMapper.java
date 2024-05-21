package com.example.demo;

import org.apache.ibatis.annotations.Mapper;
import tbs.framework.sql.interfaces.mappers.IFrameworkMapper;

/**
 * @author abstergo
 */
@Mapper
public interface LogDataMapper extends IFrameworkMapper<LogDataEntity, Long> {
}
