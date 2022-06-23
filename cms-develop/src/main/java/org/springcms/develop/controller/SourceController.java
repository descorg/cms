package org.springcms.develop.controller;

import org.springcms.core.mybatis.response.R;
import org.springcms.develop.entity.Source;
import org.springcms.develop.repository.SourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 数据源
 */
@Controller
@RequestMapping("/source")
public class SourceController {

    @Autowired
    private SourceRepository sourceRepository;

    @GetMapping("")
    public String index(Model model) {
        List<Source> sourceList = sourceRepository.findAll();
        model.addAttribute("list", sourceList);
        return "source/index";
    }

    @GetMapping("/edit")
    public String edit(Model model, @RequestParam(required = false, defaultValue = "0") Integer id) {
        Source source = null;
        if (id > 0 && sourceRepository.existsById(id)) {
            source = sourceRepository.findById(id).get();
        } else {
            source = new Source();
        }
        model.addAttribute("source", source);
        return "source/edit";
    }

    @PostMapping("")
    @ResponseBody
    public R<String> save(@RequestBody Source source) {
        source.setCreateTime(new Date());
        sourceRepository.save(source);
        return R.success("数据已保存");
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public R<String> delete(@PathVariable Integer id) {
        sourceRepository.deleteById(id);
        return R.success("数据已删除");
    }
}
