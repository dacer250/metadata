package com.ryo.metadata.core.service.impl;

import com.ryo.medata.util.util.DBSqlUtil;
import com.ryo.metadata.core.constant.EntityConstant;
import com.ryo.metadata.core.dal.DBMapper;
import com.ryo.metadata.core.dal.JdbcMapper;
import com.ryo.metadata.core.domain.MetaField;
import com.ryo.metadata.core.domain.MetaModel;
import com.ryo.metadata.core.service.DBService;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bbhou on 2017/8/2.
 */
public abstract class AbstractDBService implements DBService {

    /**
     * db 数据库访问层
     * @return
     */
    protected abstract DBMapper getDbMapper();

    /**
     * jdbc 数据库访问层
     * @return
     */
    protected abstract JdbcMapper getJdbcMapper();

    @Override
    public void createMetaModelData() throws IllegalAccessException, SQLException {
        List<MetaModel> metaModelList = getDbMapper().selectAllTables();

        List<String> sqlList = new LinkedList<>();
        String truncateTableSql = DBSqlUtil.truncateTable(EntityConstant.META_MODEL);
        sqlList.add(truncateTableSql);
        for (MetaModel metaModel : metaModelList) {
            String insertSql = DBSqlUtil.insert(EntityConstant.META_MODEL, metaModel, null);
            sqlList.add(insertSql);
        }

        getJdbcMapper().executeTransaction(sqlList);
    }

    @Override
    public void createMetaFieldData() throws IllegalAccessException, SQLException {
        List<MetaModel> metaModelList = getDbMapper().selectAllTables();
        List<String> sqlList = new LinkedList<>();
        String truncateTableSql = DBSqlUtil.truncateTable(EntityConstant.META_FIELD);
        sqlList.add(truncateTableSql);
        for (MetaModel model : metaModelList) {
            String name = model.getName();
            List<MetaField> metaFieldList = getDbMapper().selectAllFields(name);
            for (MetaField metaField : metaFieldList) {
                String insertSql = DBSqlUtil.insert(EntityConstant.META_FIELD, metaField, null);
                sqlList.add(insertSql);
            }
        }
        getJdbcMapper().executeTransaction(sqlList);
    }
}
