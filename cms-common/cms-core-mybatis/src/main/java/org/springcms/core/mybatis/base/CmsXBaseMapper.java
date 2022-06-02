package org.springcms.core.mybatis.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *
 * @param <T> entity
 * @param <K> 自增字段类型
 */
public interface CmsXBaseMapper<T, K> extends BaseMapper<T> {

    @Select("select table_name from information_schema.tables where TABLE_SCHEMA=(select database())")
    List<String> getTablesMySQL();

    @Select("select name from sysobjects where xtype='U' order by name asc")
    List<String> getTablesSqlServer();

    @Select("SELECT LAST_INSERT_ID()")
    K getLastInsertIdMySQL();

    @Select("select IDENT_CURRENT(#{table})")
    K getLastInsertIdSqlServer(@Param("table") String table);
}
