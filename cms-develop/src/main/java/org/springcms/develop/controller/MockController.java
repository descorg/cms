package org.springcms.develop.controller;

import com.alibaba.druid.pool.DruidDataSource;
import org.springcms.core.mybatis.response.R;
import org.springcms.develop.entity.Source;
import org.springcms.develop.entity.Table;
import org.springcms.develop.repository.SourceRepository;
import org.springcms.develop.repository.TableRepository;
import org.springcms.develop.utils.SqlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/mock")
public class MockController {

    @Autowired
    private SourceRepository sourceRepository;
    @Autowired
    private TableRepository tableRepository;

    @PostMapping("/{ids}")
    public R<Boolean> create(@PathVariable String ids) {
        Random r = new Random();
        for (String id : ids.split(",")) {
            Table table = tableRepository.getOne(Integer.valueOf(id));
            Source source = sourceRepository.getOne(table.getSourceId());

            try {
                if (!SqlUtils.createMockData(source, table, r.nextInt(200) + 20)) {
                    return R.fail("创建模拟数据失败");
                }
                return R.data(true, "模拟数据已创建");
            } catch (Exception e) {
                return R.fail(e.getMessage());
            }
        }
        return R.fail("创建模拟数据失败");
    }

    @DeleteMapping("/{ids}")
    public R<Boolean> clear(@PathVariable String ids) {
        for (String id : ids.split(",")) {
            Table table = tableRepository.getOne(Integer.valueOf(id));
            Source source = sourceRepository.getOne(table.getSourceId());

            DruidDataSource dataSource = SqlUtils.getDataSource(source);
            if (dataSource == null) {
                R.fail("数据源配置有误");
            }
            JdbcTemplate jdbcTemplate = SqlUtils.getJdbcTemplate(dataSource);
            if (jdbcTemplate == null) {
                R.fail("创建jdbcTemplate失败");
            }

            String sql = String.format("delete from %s%s", table.getPrefix() == null ? "" : table.getPrefix(), table.getName());
            jdbcTemplate.execute(sql);
        }
        return R.data(true, "数据已清空");
    }
}
