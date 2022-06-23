package org.springcms.develop.utils;

import com.alibaba.fastjson.JSONArray;
import org.springcms.develop.entity.Code;
import org.springcms.develop.entity.Field;
import org.springcms.develop.entity.Table;

import java.util.List;

public class CodeUtils {
    /**
     * 生成代码
     * @param table
     * @param code
     * @return
     * @throws Exception
     */
    public static Boolean generateCode(Table table, Code code) throws Exception {
        TemplateUtils.process("code/back/BeanConfig.java", String.format("%s/java/%s/config/BeanConfig.java", code.getBack(), code.getWrap()),
                code.getWrap(),
                code.getWrap(), code.getEntity(),
                code.getWrap(), code.getEntity(),
                code.getEntity(), code.getEntity().substring(0,1).toLowerCase().concat(code.getEntity().substring(1)),
                code.getEntity());

        TemplateUtils.process("code/back/Controller.java", String.format("%s/java/%s/controller/%sController.java", code.getBack(), code.getWrap(), code.getEntity()),
                code.getWrap(),
                code.getWrap(), code.getEntity(),
                code.getEntity().toLowerCase(),
                code.getEntity(), table.getDescription(),
                code.getEntity(), code.getEntity());

        StringBuffer xml = new StringBuffer();
        StringBuffer body = new StringBuffer();
        JSONArray jsonArray = JSONArray.parseArray(table.getFields());
        List<Field> fieldList = jsonArray.toJavaList(Field.class);
        for (Field field : fieldList) {
            String fieldName = StringUtils.generateClassName(field.getName());
            String property = fieldName.substring(0,1).toLowerCase().concat(fieldName.substring(1));
            body.append(String.format("\t@ApiModelProperty(value = \"%s\", example = \"%s\")\n", field.getDescription() == null ? "" : field.getDescription(), field.getValue() == null ? "" : field.getValue()));
            body.append("\t").append("private ").append(dataType2JavaType(field.getType())).append(" ").append(property).append(";\n\n");

            xml.append(String.format("\t\t<result property=\"%s\" column=\"%s\" jdbcType=\"%s\"/>\n", property, field.getName(), dataType2JdbcType(field.getType())));
        }
        TemplateUtils.process("code/back/Entity.java", String.format("%s/java/%s/entity/%s.java", code.getBack(), code.getWrap(), code.getEntity()),
                code.getWrap(),
                table.getPrefix(), table.getName(),
                code.getEntity(), table.getDescription(),
                code.getEntity(),
                body.toString());

        TemplateUtils.process("code/back/Mapper.java", String.format("%s/java/%s/mapper/%sMapper.java", code.getBack(), code.getWrap(), code.getEntity()),
                code.getWrap(),
                code.getWrap(), code.getEntity(),
                code.getEntity(), code.getEntity());

        TemplateUtils.process("code/back/Mapper.xml", String.format("%s/java/%s/mapper/%sMapper.xml", code.getBack(), code.getWrap(), code.getEntity()),
                code.getWrap(), code.getEntity(),
                code.getWrap(), code.getEntity(),
                xml.toString());

        TemplateUtils.process("code/back/Service.java", String.format("%s/java/%s/service/%sService.java", code.getBack(), code.getWrap(), code.getEntity()),
                code.getWrap(),
                code.getWrap(), code.getEntity(),
                code.getWrap(), code.getEntity(),
                code.getEntity(), code.getEntity(), code.getEntity());

        TemplateUtils.process("code/back/ServiceImpl.java", String.format("%s/java/%s/service/impl/%sServiceImpl.java", code.getBack(), code.getWrap(), code.getEntity()),
                code.getWrap(),
                code.getWrap(), code.getEntity(),
                code.getWrap(), code.getEntity(),
                code.getWrap(), code.getEntity(),
                code.getWrap(),
                code.getEntity(), code.getEntity(), code.getEntity(), code.getEntity());

        TemplateUtils.process("code/back/Wrapper.java", String.format("%s/java/%s/wrapper/%sWrapper.java", code.getBack(), code.getWrap(), code.getEntity()),
                code.getWrap(),
                code.getWrap(), code.getEntity(),
                code.getEntity(), code.getEntity(),
                code.getEntity(), code.getEntity());

        return true;
    }

    /**
     * 数据库类型转java类型
     * @param dataType
     * @return
     */
    public static String dataType2JavaType(String dataType) {
        if (dataType.equalsIgnoreCase("tinyint")) {
            return "Integer";
        } else if (dataType.equalsIgnoreCase("decimal")) {
            return "BigDecimal";
        } else if (dataType.equalsIgnoreCase("datetime")) {
            return "Date";
        }
        return "String";
    }

    /**
     * 数据库类型转jdbc类型
     * @param dataType
     * @return
     */
    public static String dataType2JdbcType(String dataType) {
        if (dataType.equalsIgnoreCase("int")) {
            return "INTEGER";
        } else if (dataType.indexOf("char") != -1) {
            return "VARCHAR";
        } else if (dataType.equalsIgnoreCase("decimal")) {
            return "DECIMAL";
        } else if (dataType.equalsIgnoreCase("datetime")) {
            return "TIMESTAMP";
        } else if (dataType.equalsIgnoreCase("tinyint")) {
            return "TINYINT";
        }
        return "VARCHAR";
    }
}
