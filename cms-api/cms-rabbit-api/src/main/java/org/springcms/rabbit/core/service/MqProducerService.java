package org.springcms.rabbit.core.service;

import org.springcms.rabbit.core.entity.MqProducer;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【cms_mq_producer】的数据库操作Service
* @createDate 2022-06-11 16:17:41
*/
public interface MqProducerService extends IService<MqProducer> {

    /**
     * 更新消息状态
     * @param uuid
     * @param status
     * @return
     */
    Boolean updateQueueStatus(String uuid, Integer status);

    /**
     * 获取未发送成功记录
     * @param size
     * @return
     */
    List<MqProducer> getNotSentList(Integer size);
}
