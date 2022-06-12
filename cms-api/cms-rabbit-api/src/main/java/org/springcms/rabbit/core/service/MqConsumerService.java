package org.springcms.rabbit.core.service;

import org.springcms.rabbit.core.entity.MqConsumer;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【cms_mq_consumer】的数据库操作Service
* @createDate 2022-06-11 16:16:10
*/
public interface MqConsumerService extends IService<MqConsumer> {

    /**
     * 更新消息状态
     * @param uuid
     * @param status
     * @return
     */
    Boolean updateQueueStatus(String uuid, Integer status);
}
