package org.springcms.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springcms.utils.RedisUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/demo")
@Api(value = "DemoController", tags = "演示")
public class DemoController {

    @Resource
    RedisUtils redisUtils;

    @GetMapping("/index")
    @ApiOperation(value = "index")
    public String index() {
        redisUtils.set("cms:demo:index", "sfadfadfa", 1000L);
        return "ok";
    }
}
