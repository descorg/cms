package org.springcms.demo.controller;

import io.swagger.annotations.Api;
import org.springcms.core.mybatis.base.BaseController;
import org.springcms.demo.entity.Admin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Api(value = "AdminController", tags = "管理员")
public class AdminController extends BaseController<Admin> {

}
