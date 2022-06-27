package org.springcms.develop.utils;

import com.alibaba.fastjson.JSONArray;
import org.springcms.develop.entity.Code;
import org.springcms.develop.entity.Field;
import org.springcms.develop.entity.Table;

import java.util.ArrayList;
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
        if (code.getBack() != null && !code.getBack().isEmpty()) {
            generateCodeBack(table, code);
        }

        if (code.getFront() != null && !code.getFront().isEmpty()) {
            generateCodeFront(table, code);
        }

        return true;
    }
    private static void generateCodeBack(Table table, Code code) throws Exception {
        //跳过父类字段
        List<String> skepField = new ArrayList<>();
        skepField.add("id");
        skepField.add("is_deleted");
        skepField.add("create_user");
        skepField.add("create_time");

        TemplateUtils.process("code/back/BeanConfig.java", String.format("%s/java/%s/config/%sConfig.java", code.getBack(), code.getWrap(), code.getEntity()),
                code.getWrap(),
                code.getWrap(), code.getEntity(),
                code.getWrap(), code.getEntity(),
                code.getEntity(),
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

            if (!skepField.contains(field.getName())) {
                body.append(String.format("\t@ApiModelProperty(value = \"%s\", required = %s, example = \"%s\")\n",
                        field.getDescription() == null ? "" : field.getDescription(),
                        field.getIsNull() ? "false" : "true",
                        field.getValue() == null ? "" : field.getValue()));
//                body.append(String.format("\t@Column(name = \"%s\")\n", field.getName()));
                body.append("\t").append("private ").append(dataType2JavaType(field.getType())).append(" ").append(property).append(";\n\n");
            }

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
    }

    /**
     * 前端 - bladex文件
     * @return
     * @throws Exception
     */
    private static void generateCodeFront(Table table, Code code) throws Exception {
        JSONArray jsonArray = JSONArray.parseArray(table.getFields());
        List<Field> fieldList = jsonArray.toJavaList(Field.class);

        TemplateUtils.process("code/front/bladex/api.js", String.format("%s/bladex/api/%s/%s.js", code.getFront(), removePrefix(code.getModule()), code.getEntity().toLowerCase()),
                code.getModule(), code.getEntity().toLowerCase(),
                code.getModule(), code.getEntity().toLowerCase(),
                code.getModule(), code.getEntity().toLowerCase(),
                code.getModule(), code.getEntity().toLowerCase(),
                code.getModule(), code.getEntity().toLowerCase());

        StringBuffer column = new StringBuffer();
        for (Field field : fieldList) {
            String fieldName = StringUtils.generateClassName(field.getName());
            String property = fieldName.substring(0,1).toLowerCase().concat(fieldName.substring(1));
            if (field.getName().equals("create_time")) {
                column.append(String.format("            {\n" +
                        "              label: \"%s\",\n" +
                        "              search: true,\n" +
                        "              prop: \"%s\",\n" +
                        "              type: \"datetime\",\n" +
                        "              span: 24,\n" +
                        "              format: \"yyyy-MM-dd hh:mm:ss\",\n" +
                        "              valueFormat: \"yyyy-MM-dd hh:mm:ss\",\n" +
                        "              searchRange:true,\n" +
                        "              addDisplay: false,\n" +
                        "              editDisplay: false,\n" +
                        "              search: true,\n" +
                        "            },\n", field.getDescription(), property));
            } else {
                column.append(String.format("            {\n" +
                        "              label: \"%s\",\n" +
                        "              prop: \"%s\",\n" +
                        "              rules: [{\n" +
                        "                required: true,\n" +
                        "                message: \"请输入%s\",\n" +
                        "                trigger: \"blur\"\n" +
                        "              }]\n" +
                        "            },\n", field.getDescription(), property, field.getDescription()));
            }
        }
        TemplateUtils.process("code/front/bladex/view.vue", String.format("%s/bladex/views/%s/%s.vue", code.getFront(), removePrefix(code.getModule()), code.getEntity().toLowerCase()),
                code.getEntity().toLowerCase(),
                removePrefix(code.getModule()), code.getEntity().toLowerCase(),
                column.toString(),
                code.getEntity().toLowerCase(),
                code.getEntity().toLowerCase(),
                code.getEntity().toLowerCase(),
                code.getEntity().toLowerCase());
    }

    /**
     * 移除前缀
     * @param str
     * @return
     */
    public static String removePrefix(String str) {
        if (str.indexOf("-") != -1) {
            return str.substring(str.indexOf("-") + 1);
        }
        return str;
    }

    /**
     * 数据库类型转java类型
     * @param dataType
     * @return
     */
    public static String dataType2JavaType(String dataType) {
        if (dataType.equalsIgnoreCase("bigint")) {
            return "Long";
        } else if (dataType.equalsIgnoreCase("tinyint") || dataType.equalsIgnoreCase("int")) {
            return "Integer";
        } else if (dataType.equalsIgnoreCase("decimal")) {
            return "BigDecimal";
        } else if (dataType.equalsIgnoreCase("datetime") || dataType.equalsIgnoreCase("timestamp")) {
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
