package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tbs.framework.sql.annotations.OrField;
import tbs.framework.sql.annotations.QueryField;
import tbs.framework.sql.annotations.QueryIndex;
import tbs.framework.sql.annotations.QueryOrderField;
import tbs.framework.sql.enums.QueryConnectorEnum;
import tbs.framework.sql.enums.QueryContrastEnum;
import tbs.framework.sql.interfaces.IQuery;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysUserQO implements IQuery, Serializable {

    private static final long serialVersionUID = -6441511924462004046L;

    @Override
    public String baseQuerySql() {
        return "select * from sys_user ";
    }

    @QueryField(map = "name", index = 1, connector = QueryConnectorEnum.OR, contrast = QueryContrastEnum.LLIKE)
    @QueryField(map = "phone", index = 0, connector = QueryConnectorEnum.OR, contrast = QueryContrastEnum.LLIKE)
    @QueryIndex(index = 4)
    private String nameOrPhone;

    @QueryField
    @QueryIndex(index = 3)
    private String password;

    @QueryField
    @QueryIndex(index = 2)
    private Integer sex;

    @QueryField(contrast = QueryContrastEnum.IN)
    @OrField
    @QueryOrderField
    @QueryIndex(index = 1)
    private List<Long> id;
}
