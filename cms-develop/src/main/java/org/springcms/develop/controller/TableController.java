package org.springcms.develop.controller;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONArray;
import org.springcms.core.mybatis.response.R;
import org.springcms.develop.entity.Code;
import org.springcms.develop.entity.Field;
import org.springcms.develop.entity.Source;
import org.springcms.develop.entity.Table;
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
        Table table;
        if (id > 0 && tableRepository.existsById(id)) {
            table = tableRepository.getOne(id);
            JSONArray jsonArray = JSONArray.parseArray(table.getFields());
            fieldList = jsonArray.toJavaList(Field.class);
        } else {
            table = new Table();
        }
        model.addAttribute("fieldList", fieldList);
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
        table.setCreateTime(new Date());
        tableRepository.save(table);

        String result = null;
        if (table.getExecute()) {
            result = createTable(table);
        }
        return result == null ? R.success("数据已保存") : R.fail(result);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public R<String> delete(@PathVariable Integer id) {
        tableRepository.deleteById(id);
        return R.success("数据已删除");
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
                return R.success("代码已生成");
            } else {
                return R.fail("代码生成失败");
            }
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 创建表
     * @param table
     * @return
     */
    private String createTable(Table table) {
        DruidDataSource dataSource = SqlUtils.getDataSource(sourceRepository.getOne(table.getSourceId()));
        if (dataSource == null) {
            return "数据源不存在";
        }

        JdbcTemplate jdbcTemplate = SqlUtils.getJdbcTemplate(dataSource);
        if (jdbcTemplate == null) {
            return "数据源配置不正确";
        }

        List<String> sqlList = null;
        if (dataSource.getDriverClassName().indexOf("mysql") != -1) {
            sqlList = SqlUtils.generateMySQL(table);
        } else if (dataSource.getDriverClassName().indexOf("postgresql") != -1) {

        } else if (dataSource.getDriverClassName().indexOf("oracle") != -1) {

        } else if (dataSource.getDriverClassName().indexOf("sqlserver") != -1) {
            sqlList = SqlUtils.generateSqlServer(table);
        } else {
            return "未知的数据源";
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
