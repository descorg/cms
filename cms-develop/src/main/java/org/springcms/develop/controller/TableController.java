package org.springcms.develop.controller;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONArray;
import org.springcms.core.mybatis.response.R;
import org.springcms.develop.entity.*;
import org.springcms.develop.repository.CodeRepository;
import org.springcms.develop.repository.SourceRepository;
import org.springcms.develop.repository.TableRepository;
import org.springcms.develop.utils.CodeUtils;
import org.springcms.develop.utils.SqlUtils;
import org.springcms.develop.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/table")
public class TableController {

    @Autowired
    private SourceRepository sourceRepository;
    @Autowired
    private TableRepository tableRepository;
    @Autowired
    private CodeRepository codeRepository;

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("source", sourceRepository.findAll());
        model.addAttribute("list", tableRepository.findAll());
        return "table/index";
    }

    @GetMapping("/query/{sourceId}")
    @ResponseBody
    public R<List<Table>> query(@PathVariable Integer sourceId) {
        List<Table> result = new ArrayList<>();
        List<Table> existsTables = tableRepository.findAll();

        Source source = sourceRepository.getOne(sourceId);
        DruidDataSource dataSource = SqlUtils.getDataSource(source);
        JdbcTemplate jdbcTemplate = SqlUtils.getJdbcTemplate(dataSource);
        List<Table> tableList = null;
        if (source.getDrive().indexOf("mysql") != -1) {
            tableList = SqlUtils.exportMySQL(jdbcTemplate, source.getDatabase());
        } else if (source.getDrive().indexOf("sqlserver") != -1) {
            tableList = SqlUtils.getTablesSqlServer(jdbcTemplate);
        }

        if (tableList != null) {
            for (Table table : tableList) {
                Boolean exists = false;
                for (Table tmp : existsTables) {
                    if (tmp.getPrefix().concat(tmp.getName()).equalsIgnoreCase(table.getName()) && tmp.getSourceId().equals(sourceId)) {
                        exists = true;
                        result.add(tmp);
                        break;
                    }
                }

                if (!exists) {
                    table.setSourceId(sourceId);
                    table = tableRepository.save(table);
                    result.add(table);
                }
            }
        }

        return R.data(result, "ok");
    }

    @GetMapping("/edit")
    public String edit(Model model, @RequestParam(required = false, defaultValue = "0") Integer id) {
        List<Field> fieldList = new ArrayList<>();
        List<Summary> summaries = new ArrayList<>();
        Table table;
        if (id > 0 && tableRepository.existsById(id)) {
            table = tableRepository.getOne(id);
            if (table.getSummary() == null) {
                table.setSummary("[]");
            }

            JSONArray jsonArray1 = JSONArray.parseArray(table.getFields());
            fieldList = jsonArray1.toJavaList(Field.class);

            JSONArray jsonArray2 = JSONArray.parseArray(table.getSummary());
            summaries = jsonArray2.toJavaList(Summary.class);
        } else {
            table = new Table();
        }
        model.addAttribute("fieldList", fieldList);
        model.addAttribute("summaries", summaries);
        model.addAttribute("table", table);
        model.addAttribute("source", sourceRepository.findAll());
        return "table/edit";
    }

    @GetMapping("/code")
    public String code(Model model, @RequestParam(required = true, defaultValue = "0") Integer id) {
        Table table = tableRepository.getOne(id);
        Code code = new Code();
        if (id > 0 && codeRepository.existsById(id)) {
            code = codeRepository.getOne(id);
        } else {
            code.setId(id);
            code.setEntity(StringUtils.generateClassName(table.getName()));
        }
        model.addAttribute("code", code);
        return "table/code";
    }

    @PostMapping("")
    @ResponseBody
    public R<String> save(@RequestBody Table table) {
        List<String> fields = new ArrayList<>();
        JSONArray jsonArray = JSONArray.parseArray(table.getFields());
        List<Field> fieldList = jsonArray.toJavaList(Field.class);

        for(Field field : fieldList) {
            fields.add(field.getName());
        }

        //????????????
        if (!fields.contains("is_deleted")) {
            Field field = new Field();
            field.setName("is_deleted");
            field.setType("tinyint");
            field.setValue("0");
            field.setIsNull(true);
            field.setIsPk(false);
            field.setDescription("????????????");
            fieldList.add(field);
        }
        if (!fields.contains("create_user")) {
            Field field = new Field();
            field.setName("create_user");
            field.setType("int");
            field.setIsNull(true);
            field.setIsPk(false);
            field.setDescription("?????????");
            fieldList.add(field);
        }
        if (!fields.contains("create_time")) {
            Field field = new Field();
            field.setName("create_time");
            field.setType("datetime");
            field.setIsNull(true);
            field.setIsPk(false);
            field.setDescription("????????????");
            fieldList.add(field);
        }

        table.setFields(JSONArray.toJSONString(fieldList));
        table.setCreateTime(new Date());
        table = tableRepository.save(table);

        String result = null;
        if (table.getExecute()) {
            result = createTable(table);
        }
        return result == null ? R.success("???????????????") : R.fail(result);
    }

    @DeleteMapping("/{ids}")
    @ResponseBody
    public R<String> delete(@PathVariable String ids) {
        if (ids.indexOf(",") != -1) {
            for (String id : ids.split(",")) {
                tableRepository.deleteById(Integer.valueOf(id));
            }
        } else {
            tableRepository.deleteById(Integer.valueOf(ids));
        }
        return R.success("???????????????");
    }

    @PostMapping("/code")
    @ResponseBody
    public R<String> saveCode(@RequestBody Code code) {
        try {
            code.setWrap(code.getWrap().toLowerCase());
            code.setEntity(code.getEntity().substring(0,1).toUpperCase().concat(code.getEntity().substring(1)));
            codeRepository.save(code);
            Table table = tableRepository.getOne(code.getId());
            if (CodeUtils.generateCode(table, code)) {
                return R.success("???????????????");
            } else {
                return R.fail("??????????????????");
            }
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * ?????????
     * @param table
     * @return
     */
    private String createTable(Table table) {
        DruidDataSource dataSource = SqlUtils.getDataSource(sourceRepository.getOne(table.getSourceId()));
        if (dataSource == null) {
            return "??????????????????";
        }

        JdbcTemplate jdbcTemplate = SqlUtils.getJdbcTemplate(dataSource);
        if (jdbcTemplate == null) {
            return "????????????????????????";
        }

        List<String> sqlList = null;
        if (dataSource.getDriverClassName().indexOf("mysql") != -1) {
            sqlList = SqlUtils.generateMySQL(table);
        } else if (dataSource.getDriverClassName().indexOf("postgresql") != -1) {

        } else if (dataSource.getDriverClassName().indexOf("oracle") != -1) {

        } else if (dataSource.getDriverClassName().indexOf("sqlserver") != -1) {
            sqlList = SqlUtils.generateSqlServer(table);
        } else {
            return "??????????????????";
        }

        try {
            if (sqlList != null) {
                for (String sql : sqlList) {
                    jdbcTemplate.execute(sql);
                }
            }
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
