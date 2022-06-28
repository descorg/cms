package org.springcms.develop.controller;

import com.alibaba.fastjson.JSONArray;
import org.springcms.core.mybatis.response.R;
import org.springcms.develop.entity.Field;
import org.springcms.develop.entity.Table;
import org.springcms.develop.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/field")
public class FieldController {

    @Autowired
    private TableRepository tableRepository;

    @GetMapping("")
    public R<List<Field>> query(@RequestParam Integer sourceId, @RequestParam String table) {
        Table table2 = new Table();
        table2.setSourceId(sourceId);
        Example<Table> example = Example.of(table2);
        List<Table> tableList = tableRepository.findAll(example);

        for (Table table1 : tableList) {
            if (table1.getSourceId().equals(sourceId) && table1.getPrefix().concat(table1.getName()).equals(table)) {
                JSONArray jsonArray = JSONArray.parseArray(table1.getFields());
                List<Field> fieldList = jsonArray.toJavaList(Field.class);
                return R.data(fieldList, "ok");
            }
        }
        return R.fail("查询失败");
    }
}
