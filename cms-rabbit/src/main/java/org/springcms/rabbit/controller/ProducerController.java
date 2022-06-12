package org.springcms.rabbit.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springcms.rabbit.core.entity.MqProducer;
import org.springcms.core.mybatis.response.R;
import org.springcms.core.mybatis.vo.Query;
import org.springcms.rabbit.core.service.MqProducerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/producer")
@Api(value = "ProducerController", tags = "发送者")
public class ProducerController {

    @Resource
    private MqProducerService producerService;

    @GetMapping("")
    @ApiOperation(value = "查询发送记录")
    public R<IPage<MqProducer>> query(MqProducer producer, Query query) {
        LambdaQueryWrapper<MqProducer> lqw = new LambdaQueryWrapper<>(producer);
        if (producer.getStartTime() != null && !producer.getStartTime().isEmpty()) {
            lqw.gt(MqProducer::getSendTime, producer.getStartTime());
        }
        if (producer.getEndTime() != null && !producer.getEndTime().isEmpty()) {
            lqw.lt(MqProducer::getSendTime, producer.getEndTime());
        }
        lqw.orderByDesc(MqProducer::getSendTime);
        IPage<MqProducer> producerIPage = producerService.page(new Page<>(query.getCurrent(), query.getSize()), lqw);
        return R.data(producerIPage, "ok");
    }
}
