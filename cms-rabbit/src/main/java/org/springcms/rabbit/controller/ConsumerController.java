package org.springcms.rabbit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springcms.rabbit.core.entity.MqConsumer;
import org.springcms.core.mybatis.response.R;
import org.springcms.core.mybatis.vo.Query;
import org.springcms.rabbit.core.service.MqConsumerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/consumer")
@Api(value = "ConsumerController", tags = "接收者")
public class ConsumerController {

    @Resource
    private MqConsumerService consumerService;

    @GetMapping("")
    @ApiOperation(value = "查询接收记录")
    public R<IPage<MqConsumer>> query(MqConsumer consumer, Query query) {
        LambdaQueryWrapper<MqConsumer> lqw = new LambdaQueryWrapper<>(consumer);
        if (consumer.getStartTime() != null && !consumer.getStartTime().isEmpty()) {
            lqw.gt(MqConsumer::getRecvTime, consumer.getStartTime());
        }
        if (consumer.getEndTime() != null && !consumer.getEndTime().isEmpty()) {
            lqw.lt(MqConsumer::getRecvTime, consumer.getEndTime());
        }
        lqw.orderByDesc(MqConsumer::getRecvTime);
        IPage<MqConsumer> consumerIPage = consumerService.page(new Page<>(query.getCurrent(), query.getSize()), lqw);
        return R.data(consumerIPage, "ok");
    }
}
