package org.springcms.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springcms.core.mybatis.base.BaseController;
import org.springcms.core.mybatis.response.R;
import org.springcms.demo.entity.Admin;
import org.springcms.demo.service.AdminService;
import org.springcms.demo.vo.LoginVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin")
@Api(value = "AdminController", tags = "管理员")
public class AdminController extends BaseController<Admin> {

    @Resource
    AdminService adminService;

    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public R<LoginVO> login(@RequestParam(name = "登录名") String userName, @RequestParam(name = "登录密码", required = true) String userPass) throws Exception {
        return R.data(adminService.Login(userName, userPass));
    }
}
