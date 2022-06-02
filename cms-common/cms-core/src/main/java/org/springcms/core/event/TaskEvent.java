package org.springcms.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TaskEvent extends Thread {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected Object data;

    /**
     * 具体作业
     * @throws Exception
     */
    abstract void task() throws Exception;

    /**
     * 开始执行前
     * @return 是否继续执行后面的任务
     */
    public boolean onBefore() {
        logger.debug(String.format("%s onBefore", this.getClass().getName()));
        return true;
    }

    /**
     * 执行成功
     */
    public void onSuccess() {
        logger.debug(String.format("%s onSuccess", this.getClass().getName()));
    }

    /**
     * 执行失败
     * @param message
     */
    public void onError(String message) {
        logger.debug(String.format("%s onError:%s", this.getClass().getName(), message));
    }

    @Override
    public void run() {
        if (this.onBefore()) {
            try {
                this.task();
                this.onSuccess();
            } catch (Exception e) {
                e.printStackTrace();
                this.onError(e.getLocalizedMessage());
            }
        }
    }

    /**
     * 启动
     * @param data
     */
    public void start(Object data) {
        this.data = data;
        super.start();
    }
}
