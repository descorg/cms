package org.springcms.rabbit.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcms.rabbit.core.entity.MqConsumer;
import org.springcms.rabbit.core.mapper.MqConsumerMapper;
import org.springcms.rabbit.core.service.MqConsumerService;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【cms_mq_consumer】的数据库操作Service实现
* @createDate 2022-06-11 16:16:10
*/
@Service
public class MqConsumerServiceImpl extends ServiceImpl<MqConsumerMapper, MqConsumer> implements MqConsumerService {

    @Override
    public Boolean updateQueueStatus(String uuid, Integer status) {
        LambdaQueryWrapper<MqConsumer> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MqConsumer::getUuid, uuid);

        MqConsumer consumer = getOne(lqw);
        if (consumer != null && !consumer.getStatus().equals(status)) {
            consumer.setStatus(status);
            return updateById(consumer);
        }

        return false;
    }
}




