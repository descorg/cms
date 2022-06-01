package org.springcms.rabbit.event;

import org.springcms.vo.QueueVO;

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
