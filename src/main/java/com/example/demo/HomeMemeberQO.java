package com.example.demo;

import lombok.Data;
import tbs.framework.sql.annotations.QueryField;
import tbs.framework.sql.annotations.QueryOrderModel;
import tbs.framework.sql.enums.QueryContrastEnum;
import tbs.framework.sql.interfaces.IQuery;
import tbs.framework.sql.model.OrderedModel;

import java.util.List;

/**
 * @author abstergo
 */
@Data
public class HomeMemeberQO implements IQuery {
    private String baseQuerySql = "SELECT * FROM(SELECT\n" +
        "\tid,homeId,enabled,type,userId,userName\n" +
        "FROM\n" +
        "\t(\n" +
        "\tSELECT\n" +
        "\t\thm.id AS id,\n" +
        "\t\thm.home_id AS homeId,\n" +
        "\t\thm.ENABLE AS enabled,\n" +
        "\t\thm.member_type AS type,\n" +
        "\t\thm.child_id AS userId,\n" +
        "\t\tu.NAME AS userName \n" +
        "\tFROM\n" +
        "\t\tuser_rights ur\n" +
        "\t\tLEFT JOIN api_rights ar ON 1 = 1 \n" +
        "\t\tAND ar.id = ur.rights_id\n" +
        "\t\tLEFT JOIN user_right_param urp ON urp.user_right_id = ur.id\n" +
        "\t\tLEFT JOIN home_member hm ON hm.child_id = ur.user_id \n" +
        "\t\tAND hm.home_id = urp.number_value \n" +
        "\t\tAND hm.`enable` = 1\n" +
        "\t\tLEFT JOIN sys_user u ON u.id = hm.child_id \n" +
        "\tWHERE\n" +
        "\t\tar.id IS NOT NULL \n" +
        "\tGROUP BY\n" +
        "\t\thm.id \n" +
        "\t) l1 UNION ALL\n" +
        "\t(\n" +
        "\tSELECT\n" +
        "\t\thm.id AS id,\n" +
        "\t\thm.home_id AS homeId,\n" +
        "\t\thm.ENABLE AS enabled,\n" +
        "\t\thm.member_type AS type,\n" +
        "\t\thm.child_id AS userId,\n" +
        "\t\tu.NAME AS userName \n" +
        "\tFROM\n" +
        "\t\thome_member hm\n" +
        "\t\tLEFT JOIN sys_user u ON u.id = hm.child_id \n" +
        "\tWHERE\n" +
        "\t\t hm.member_type = '0' \n" +
        "\t) ) total";

    @Override
    public String baseQuerySql() {
        return baseQuerySql;
    }

    @QueryField(contrast = QueryContrastEnum.LLIKE)
    private String userName;

    @QueryField(contrast = QueryContrastEnum.IN, map = "homeId")
    private List<Long> homeIds;

    @QueryField
    private Integer type;
    
    @QueryOrderModel
    OrderedModel userId;
}
