package com.example.demo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import tbs.framework.sql.annotations.QueryField;
import tbs.framework.sql.enums.QueryContrastEnum;
import tbs.framework.sql.interfaces.IQuery;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class LoginInfoQO implements IQuery, Serializable {

    private static final long serialVersionUID = -3298770517941586663L;

    @QueryField(map = "login_time", contrast = QueryContrastEnum.GREATER)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date begin;

    @QueryField(map = "login_time", contrast = QueryContrastEnum.LESS)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime end;

    @Override
    public String baseQuerySql() {
        return "select * from login_info";
    }
}
