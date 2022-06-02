package org.springcms.demo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springcms.core.rabbit.event.SendStateListener;
import org.springcms.core.vo.QueueVO;

public class mySendStateListener implements SendStateListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

}
