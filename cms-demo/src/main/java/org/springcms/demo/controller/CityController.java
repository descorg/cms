package org.springcms.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springcms.core.mybatis.base.BaseController;
import org.springcms.demo.entity.City;
import org.springcms.demo.service.CityService;
import org.springcms.demo.vo.TreeCityVO;
import org.springcms.core.mybatis.response.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/city")
@Api(value = "CityController", tags = "City")
public class CityController extends BaseController<City> {
    @Resource
    CityService cityService;

    @GetMapping("/tree/{id}")
    @ApiModelProperty("树形数据")
    public R<List<TreeCityVO>> tree(@PathVariable Long id) {
        return R.data(cityService.tree(id), "ok");
    }
}
