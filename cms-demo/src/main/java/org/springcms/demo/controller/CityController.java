package org.springcms.demo.controller;

import io.swagger.annotations.Api;
import org.springcms.core.mybatis.base.BaseController;
import org.springcms.demo.entity.City;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/city")
@Api(value = "CityController", tags = "City")
public class CityController extends BaseController<City> {
}
