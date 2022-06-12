package org.springcms.rabbit.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcms.rabbit.core.entity.MqProducer;
import org.springcms.rabbit.core.mapper.MqProducerMapper;
import org.springcms.rabbit.core.service.MqProducerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【cms_mq_producer】的数据库操作Service实现
* @createDate 2022-06-11 16:17:41
*/
@Service
public class MqProducerServiceImpl extends ServiceImpl<MqProducerMapper, MqProducer> implements MqProducerService {

    @Override
    public Boolean updateQueueStatus(String uuid, Integer status) {
        LambdaQueryWrapper<MqProducer> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MqProducer::getUuid, uuid);
        MqProducer producer = getOne(lqw);
        if (producer == null) {
            return false;
        }

        if (!producer.getStatus().equals(status)) {
            producer.setStatus(status);
            updateById(producer);
        }

        return true;
    }

    @Override
    public List<MqProducer> getNotSentList(Integer size) {
        LambdaQueryWrapper<MqProducer> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MqProducer::getStatus, MqProducer.QueueStatue.QUEUE_STATUE_NOTSENT.ordinal());
        lqw.orderByDesc(MqProducer::getSendTime);

        IPage<MqProducer> producerIPage = new Page<>(1, size);

        producerIPage = this.page(producerIPage, lqw);

        return producerIPage.getRecords();
    }
}




