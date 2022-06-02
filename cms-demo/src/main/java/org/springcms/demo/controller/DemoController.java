package org.springcms.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springcms.jwt.utils.JwtUtils;
import org.springcms.rabbit.event.SendStateListener;
import org.springcms.rabbit.utils.RabbitUtils;
import org.springcms.utils.RedisUtils;
import org.springcms.vo.QueueVO;
import org.springframework.amqp.rabbit.connection.CorrelationData;
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

    private class mySendStateListener implements SendStateListener {

        @Override
        public void onBefore(QueueVO queue) {
            logger.info("发送之前{}", queue);
        }

        @Override
        public void onComplete(String uuid) {
            logger.info("发送完成{}", uuid);
        }

        @Override
        public void onError(String uuid, String error) {
            logger.info("发送出错{}", uuid, error);
        }
    };
}
