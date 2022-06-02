package org.springcms.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springcms.core.jwt.utils.JwtUtils;
import org.springcms.core.rabbit.utils.RabbitUtils;
import org.springcms.core.redis.utils.RedisUtils;
import org.springcms.demo.listener.mySendStateListener;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/test")
@Api(value = "DemoController", tags = "演示")
public class DemoController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        rabbitUtils.send("error",
                "{\"sites\":{\"site\":[{\"id\":\"1\",\"name\":\"菜鸟教程\",\"url\":\"www.runoob.com\"},{\"id\":\"2\",\"name\":\"菜鸟工具\",\"url\":\"c.runoob.com\"},{\"id\":\"3\",\"name\":\"Google\",\"url\":\"www.google.com\"}]}}",
                new mySendStateListener());
        return "ok";
    }

    @GetMapping("/rabbit/{source}")
    @ApiOperation(value = "rabbit")
    public String rabbit2(@PathVariable String source, @RequestParam String body) {
        rabbitUtils.create(source);
        rabbitUtils.send(source, body, new mySendStateListener());
        return "ok";
    }

    @GetMapping("/login/{uid}")
    @ApiOperation(value = "login")
    public String login(@PathVariable Long uid) {
        JwtUtils.addAccessToken(String.valueOf(uid), "afadfasdfadfsdadfadfadfadf", 600);
        return "ok";
    }
}
