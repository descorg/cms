package org.springcms.core.rabbit.event;

import org.springcms.core.vo.QueueVO;

/**
 * 消息向RabbitMQ发送时，对状态进行监听
 */
public interface SendStateListener {
    /**
     * 发送之前
     */
    void onBefore(QueueVO queue);

    void onComplete(String uuid);

    void onError(String uuid, String error);
}
