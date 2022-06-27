package org.springcms.develop.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONArray;
import org.springcms.develop.entity.Field;
import org.springcms.develop.entity.Source;
import org.springcms.develop.entity.Table;
import org.springcms.develop.vo.ReptileVO;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class SqlUtils {

    /**
     * 生成MySQL语句
     * @param table
     * @return
     */
    public static List<String> generateMySQL(Table table) {
        List<String> fields = new ArrayList<>();
        StringBuffer sb = new StringBuffer();

        sb.append(String.format("DROP TABLE IF EXISTS `%s%s`;", table.getPrefix(), table.getName())).append("\n");
        sb.append(String.format("CREATE TABLE `%s%s`  (", table.getPrefix(), table.getName())).append("\n");

        JSONArray jsonArray = JSONArray.parseArray(table.getFields());
        List<Field> fieldList = jsonArray.toJavaList(Field.class);

        for(Field field : fieldList) {
            fields.add(field.getName());
            sb.append(String.format("  `%s` %s", field.getName(), field.getType()));
            if (field.getLength() != null) {
                if (field.getDecimal() != null) {
                    sb.append(String.format("(%d,%d)", field.getLength(), field.getDecimal()));
                } else {
                    sb.append(String.format("(%d)", field.getLength()));
                }
            }

            if (field.getIsPk()) {
                sb.append(" NOT NULL AUTO_INCREMENT");

                if (field.getDescription() != null) {
                    sb.append(String.format(" COMMENT '%s'", field.getDescription()));
                }

                sb.append(",\n").append(String.format("  PRIMARY KEY (`%s`) USING BTREE,", field.getName()));
            } else {
                if (field.getIsNull()) {
                    sb.append(" NULL");
                } else {
                    sb.append(" NOT NULL");
                }

                if (field.getValue() != null && !field.getValue().isEmpty()) {
                    sb.append(String.format(" DEFAULT %s", field.getValue()));
                }

                if (field.getDescription() != null && !field.getDescription().isEmpty()) {
                    sb.append(String.format(" COMMENT '%s'", field.getDescription()));
                }
                sb.append(",");
            }
            sb.append("\n");
        }

        //父类字段
        if (!fields.contains("is_deleted")) {
            sb.append("`is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',\n");
        }
        if (!fields.contains("create_user")) {
            sb.append("`create_user` int DEFAULT NULL COMMENT '创建者',\n");
        }
        if (!fields.contains("create_time")) {
            sb.append("create_time datetime NOT NULL COMMENT '创建时间',\n");
        }

        sb = new StringBuffer(sb.substring(0, sb.length() - 2));
        sb.append("\n").append(String.format(") ENGINE = InnoDB CHARACTER SET = utf8 COMMENT = '%s';", table.getDescription())).append("\n");

        List<String> sqlList = new ArrayList<>();
        sqlList.add(sb.toString());
        return sqlList;
    }

    /**
     * 生成SqlServer语句
     * @param table
     * @return
     */
    public static List<String> generateSqlServer(Table table) {
        List<String> fields = new ArrayList<>();
        List<String> sqlList = new ArrayList<>();
        StringBuffer sb = new StringBuffer();

        sb.append(String.format("IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[%s%s]') AND type IN ('U'))", table.getPrefix(), table.getName())).append("\n");
        sb.append(String.format("\tDROP TABLE [dbo].[%s%s]", table.getPrefix(), table.getName())).append("\n");
        sqlList.add(sb.toString());

        sb = new StringBuffer();
        sb.append(String.format("CREATE TABLE [dbo].[%s%s] (", table.getPrefix(), table.getName())).append("\n");

        JSONArray jsonArray = JSONArray.parseArray(table.getFields());
        List<Field> fieldList = jsonArray.toJavaList(Field.class);

        for(Field field : fieldList) {
            fields.add(field.getName());
            sb.append(String.format("  [%s] %s", field.getName(), field.getType()));

            if (field.getIsPk()) {
                sb.append(" IDENTITY(1,1) NOT NULL,\n");
                sb.append(String.format("CONSTRAINT [PK__%s%s__%d] PRIMARY KEY CLUSTERED ([%s])\n" +
                        "WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  \n" +
                        "ON [PRIMARY]", table.getPrefix(), table.getName(), System.currentTimeMillis(), field.getName()));
            } else {
                if (field.getValue() != null && !field.getValue().isEmpty()) {
                    sb.append(String.format(" DEFAULT ((%s))", field.getValue()));
                }

                if (field.getIsNull()) {
                    sb.append(" NULL");
                } else {
                    sb.append(" NOT NULL");
                }
            }

            sb.append(",\n");
        }

        //父类字段
        if (!fields.contains("is_deleted")) {
            sb.append("[is_deleted] tinyint DEFAULT ((0)) NULL,\n");
        }
        if (!fields.contains("create_user")) {
            sb.append("[create_user] int DEFAULT ((0)) NULL,\n");
        }
        if (!fields.contains("create_time")) {
            sb.append("[create_time] datetime  NOT NULL,\n");
        }

        sb = new StringBuffer(sb.substring(0, sb.length() - 2));
        sb.append("\n").append(")").append("ON [PRIMARY]\n").append("\n");
        sqlList.add(sb.toString());

        for(Field field : fieldList) {
            if (field.getDescription() != null && !field.getDescription().isEmpty()) {
                sb = new StringBuffer();
                sb.append(String.format("EXEC sp_addextendedproperty\n" +
                        "'MS_Description', N'%s',\n" +
                        "'SCHEMA', N'dbo',\n" +
                        "'TABLE', N'%s%s',\n" +
                        "'COLUMN', N'%s'\n" +
                        "\n", field.getDescription(), table.getPrefix(), table.getName(), field.getName()));
                sqlList.add(sb.toString());
            }
        }

        return sqlList;
    }

    /**
     * 导出数据库结构
     * @param jdbcTemplate
     * @param schema
     * @return
     */
    public static List<Table> exportMySQL(JdbcTemplate jdbcTemplate, String schema) {
        List<Table> tableList = getTablesMySQL(jdbcTemplate, schema);
        for (int i=0; i<tableList.size(); i++) {
            List<Field> fieldList = getFieldsMySQL(jdbcTemplate, schema, String.format("%s%s", tableList.get(i).getPrefix(), tableList.get(i).getName()));
            tableList.get(i).setFields(JSONArray.toJSONString(fieldList));
        }
        return tableList;
    }

    public static List<Table> exportSqlServer(JdbcTemplate jdbcTemplate) {
        List<Table> tableList = getTablesSqlServer(jdbcTemplate);
        for (int i=0; i<tableList.size(); i++) {
            List<Field> fieldList = getFieldsSqlServer(jdbcTemplate, String.format("%s%s", tableList.get(i).getPrefix(), tableList.get(i).getName()));
            tableList.get(i).setFields(JSONArray.toJSONString(fieldList));
        }
        return tableList;
    }

    /**
     * 获取所有表名
     * @param jdbcTemplate
     * @param schema
     * @return
     */
    public static List<Table> getTablesMySQL(JdbcTemplate jdbcTemplate, String schema) {
        String sql = String.format("select table_name,table_comment from information_schema.tables where table_schema='%s' order by table_name asc;", schema);
        return (List<Table>)jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                List<Table> tables = new ArrayList<>();
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    Table table = new Table();
                    table.setPrefix("");
                    table.setName(resultSet.getString("table_name"));
                    table.setDescription(resultSet.getString("table_comment"));
                    table.setCreateTime(new Date());
                    tables.add(table);
                }
                return tables;
            }
        });
    }

    public static List<Table> getTablesSqlServer(JdbcTemplate jdbcTemplate) {
        String sql = "SELECT tbs.name as table_name,ds.value as table_comment FROM sys.extended_properties ds LEFT JOIN sysobjects tbs ON ds.major_id=tbs.id WHERE ds.minor_id=0 order by tbs.name asc";
        return (List<Table>)jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                List<Table> tables = new ArrayList<>();
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    Table table = new Table();
                    table.setPrefix("");
                    table.setName(resultSet.getString("table_name"));
                    table.setDescription(resultSet.getString("table_comment"));
                    table.setCreateTime(new Date());
                    tables.add(table);
                }
                return tables;
            }
        });
    }

    /**
     * 获取表中所有字段及属性
     * @param jdbcTemplate
     * @param schema
     * @param table
     * @return
     */
    public static List<Field> getFieldsMySQL(JdbcTemplate jdbcTemplate, String schema, String table) {
        String sql = String.format("select column_name,data_type,character_maximum_length,numeric_precision,numeric_scale,is_nullable,extra,column_comment,column_type,column_default from information_schema.columns where table_schema='%s' and table_name='%s' order by column_name asc;", schema, table);
        return (List<Field>)jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                List<Field> fieldList = new ArrayList<>();
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    Field field = new Field();
                    field.setName(resultSet.getString("column_name"));
                    field.setType(resultSet.getString("data_type"));

                    if (field.getType().indexOf("long") != -1) {
                        Long length = resultSet.getLong("character_maximum_length");
                        if (length != null && resultSet.getString("column_type").indexOf(String.valueOf(length)) != -1) {
                            field.setLength(length);
                        }
                    } else {
                        Integer length = resultSet.getInt("character_maximum_length");
                        if (length != null && resultSet.getString("column_type").indexOf(String.valueOf(length)) != -1) {
                            field.setLength(Long.valueOf(String.valueOf(length)));
                        }
                    }

                    Integer scale = resultSet.getInt("numeric_scale");
                    if (scale != null && scale > 0) {
                        field.setDecimal(scale);
                    }
                    field.setIsNull(resultSet.getString("is_nullable").equalsIgnoreCase("YES"));

                    String extra = resultSet.getString("extra");
                    if (extra != null && extra.equalsIgnoreCase("auto_increment")) {
                        field.setIsPk(true);
                    }

                    String value = resultSet.getString("column_default");
                    if (value != null && !value.isEmpty()) {
                        field.setValue(value);
                    }

                    field.setDescription(resultSet.getString("column_comment"));
                    fieldList.add(field);
                }
                return fieldList;
            }
        });
    }

    public static List<Field> getFieldsSqlServer(JdbcTemplate jdbcTemplate, String table) {
        String sql = String.format("SELECT c.name,TYPE_NAME(c.system_type_id) as data_type, c.max_length,c.scale,c.is_nullable,c.is_identity,cmts.text,p.value\n" +
                "FROM sys.objects obj\n" +
                "JOIN sys.columns c ON c.object_id = obj.object_id\n" +
                "LEFT JOIN sys.extended_properties p ON c.object_id = p.major_id AND p.minor_id=c.column_id\n" +
                "LEFT JOIN sys.syscomments cmts ON c.default_object_id = cmts.id\n" +
                "WHERE obj.name='%s'", table);
        return (List<Field>)jdbcTemplate.execute(sql, new PreparedStatementCallback() {
            @Override
            public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                List<Field> fieldList = new ArrayList<>();
                ResultSet resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    Field field = new Field();
                    field.setName(resultSet.getString("name"));
                    field.setType(resultSet.getString("data_type"));

                    if (field.getType().indexOf("long") != -1) {
                        Long length = resultSet.getLong("max_length");
                        field.setLength(length);
                    } else {
                        Integer length = resultSet.getInt("max_length");
                        field.setLength(Long.valueOf(String.valueOf(length)));
                    }

                    Integer scale = resultSet.getInt("scale");
                    if (scale != null && scale > 0) {
                        field.setDecimal(scale);
                    }
                    field.setIsNull(resultSet.getString("is_nullable").equalsIgnoreCase("YES"));

                    Integer extra = resultSet.getInt("is_identity");
                    if (extra == 1) {
                        field.setIsPk(true);
                    }

                    String value = resultSet.getString("text");
                    if (value != null && !value.isEmpty()) {
                        field.setValue(value);
                    }

                    field.setDescription(resultSet.getString("value"));
                    fieldList.add(field);
                }
                return fieldList;
            }
        });
    }

    /**
     * 创建模拟数据
     * @param source
     * @param table
     * @param size
     * @return
     * @throws Exception
     */
    public static Boolean createMockData(Source source, Table table, Integer size) throws Exception {
        Random r = new Random();
        DruidDataSource dataSource = getDataSource(source);
        if (dataSource == null) {
            throw new Exception("数据源配置有误");
        }
        JdbcTemplate jdbcTemplate = getJdbcTemplate(dataSource);
        if (jdbcTemplate == null) {
            throw new Exception("创建jdbcTemplate失败");
        }

        int fieldSize = 0;
        StringBuffer sb = new StringBuffer();
        StringBuffer val = new StringBuffer();
        sb.append(String.format("INSERT INTO %s%s (", table.getPrefix() == null ? "" : table.getPrefix(), table.getName()));

        JSONArray jsonArray = JSONArray.parseArray(table.getFields());
        List<Field> fieldList = jsonArray.toJavaList(Field.class);
        for (Field field : fieldList) {
            if (field.getIsPk() != null && field.getIsPk()) {
                continue;
            }
            sb.append(field.getName()).append(",");
            val.append("?,");
            fieldSize++;
        }

        sb = new StringBuffer(sb.substring(0, sb.length()-1));
        sb.append(String.format(") VALUES (%s)", val.substring(0, val.length()-1)));

        int page = 1;
        do {
            List<ReptileVO> reptileList = ReptileUtils.eastmoneyList(page);

            for (ReptileVO reptile : reptileList) {
                Object[] obj = new Object[fieldSize];
                int index = 0;

                for (Field field : fieldList) {
                    if (field.getIsPk() != null && field.getIsPk()) {
                        continue;
                    }

                    if (field.getName().equals("title")) {
                        obj[index] = reptile.getTitle();
                    } else if (field.getName().equals("name")) {
                        obj[index] = reptile.getName();
                    } else if (field.getName().equals("keywords")) {
                        obj[index] = reptile.getKeywords();
                    } else if (field.getName().equals("description")) {
                        obj[index] = reptile.getDescription();
                    } else if (field.getName().equals("thumb")) {
                        obj[index] = reptile.getImage();
                    } else if (field.getName().equals("content") || field.getName().equals("body")) {
                        obj[index] = reptile.getBody();
                    } else if (field.getName().equals("create_time")) {
                        obj[index] = reptile.getCreateTime();
                    } else if (field.getType().indexOf("char") != -1) {
                        if (field.getLength() != null && field.getLength() < reptile.getTitle().length()) {
                            obj[index] = reptile.getTitle().substring(0, Integer.valueOf(String.valueOf(field.getLength())));
                        } else {
                            obj[index] = reptile.getTitle();
                        }
                    } else if (field.getType().indexOf("text") != -1) {
                        obj[index] = reptile.getBody();
                    } else if (field.getType().indexOf("int") != -1 || field.getType().equals("decimal") || field.getType().equals("double")) {
                        obj[index] = r.nextInt(10)+1;
                    } else if (field.getType().equals("date")) {
                        obj[index] = String.format("2022-%d-%d", r.nextInt(11) + 1, r.nextInt(30) + 1);
                    } else if (field.getType().equals("time")) {
                        obj[index] = String.format("%d:%d:%d", r.nextInt(23) + 1, r.nextInt(59) + 1, r.nextInt(59) + 1);
                    } else if (field.getType().equals("datetime") || field.getType().equals("timestamp")) {
                        obj[index] = String.format("2022-%d-%d %d:%d:%d", r.nextInt(11) + 1, r.nextInt(30) + 1, r.nextInt(23) + 1, r.nextInt(59) + 1, r.nextInt(59) + 1);
                    }
                    index++;
                }
                jdbcTemplate.update(sb.toString(), obj);
            }

            if (reptileList.size() < 20 || page * 20 >= size) {
                break;
            }
            page++;
        } while (true);

        return true;
    }

    public static DruidDataSource getDataSource(Source source) {
        if (source == null) {
            return null;
        }

        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(source.getUsername());
        druidDataSource.setPassword(source.getPassword());
        druidDataSource.setUrl(source.getUrl());
        druidDataSource.setDriverClassName(source.getDrive());
        return druidDataSource;
    }

    public static JdbcTemplate getJdbcTemplate(DataSource dataSource) {
        return dataSource == null ? null : new JdbcTemplate(dataSource);
    }
}
