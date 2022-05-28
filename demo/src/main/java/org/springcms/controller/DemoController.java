package org.springcms.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springcms.utils.RabbitUtils;
import org.springcms.utils.RedisUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/demo")
@Api(value = "DemoController", tags = "演示")
public class DemoController {

    @Resource
    RedisUtils redisUtils;
    @Resource
    RabbitUtils rabbitUtils;

    @GetMapping("/redis")
    @ApiOperation(value = "redis")
    public String redis() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        redisUtils.set("cms:demo:index", sdf.format(new Date()), 1000L);
        return "ok";
    }

    @GetMapping("/rabbit")
    @ApiOperation(value = "rabbit")
    public String rabbit() {
        rabbitUtils.send("alarm", "{\"sites\":{\"site\":[{\"id\":\"1\",\"name\":\"菜鸟教程\",\"url\":\"www.runoob.com\"},{\"id\":\"2\",\"name\":\"菜鸟工具\",\"url\":\"c.runoob.com\"},{\"id\":\"3\",\"name\":\"Google\",\"url\":\"www.google.com\"}]}}");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        redisUtils.set("cms:demo:index", sdf.format(new Date()), 1000L);
        return "ok";
    }
}
