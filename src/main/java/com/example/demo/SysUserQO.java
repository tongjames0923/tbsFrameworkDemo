package com.example.demo;

import lombok.Data;
import tbs.framework.sql.annotations.OrField;
import tbs.framework.sql.annotations.QueryField;
import tbs.framework.sql.annotations.QueryOrderField;
import tbs.framework.sql.enums.QueryConnectorEnum;
import tbs.framework.sql.enums.QueryContrastEnum;
import tbs.framework.sql.interfaces.IQuery;

import java.io.Serializable;

@Data
public class SysUserQO implements IQuery, Serializable {

    private static final long serialVersionUID = -6441511924462004046L;

    public SysUserQO(String nameOrPhone, String password, Integer sex, Long id) {
        this.nameOrPhone = nameOrPhone;
        this.password = password;
        this.sex = sex;
        this.id = id;
    }

    public SysUserQO() {
    }

    @Override
    public String baseQuerySql() {
        return "select * from sys_user ";
    }

    @QueryField(map = "name", connector = QueryConnectorEnum.OR, contrast = QueryContrastEnum.LLIKE)
    @QueryField(map = "phone", connector = QueryConnectorEnum.OR, contrast = QueryContrastEnum.LLIKE)
    private String nameOrPhone;

    @QueryField
    private String password;

    @QueryField
    private Integer sex;

    @QueryField
    @OrField
    @QueryOrderField
    private Long id;
}
