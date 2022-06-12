package org.springcms.rabbit.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @TableName cms_mq_consumer
 */
@TableName(value ="cms_mq_consumer")
@Data
@ApiModel("消费者对象")
public class MqConsumer implements Serializable {
    /**
     * 消息状态
     */
    public enum QueueStatue {
        /**
         * 未处理，处理失败
         */
        QUEUE_STATUE_UNTREATED,
        /**
         * 处理中
         */
        QUEUE_STATUE_PROCESSING,
        /**
         * 已处理
         */
        QUEUE_STATUE_ALREADY,
    }

    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "自增编号", example = "0")
    private Integer id;

    /**
     * 唯一编号
     */
    @ApiModelProperty(value = "uuid", example = "")
    private String uuid;

    /**
     * 消息来源
     */
    @ApiModelProperty(value = "消息来源", example = "")
    private String source;

    /**
     * 消息接收者
     */
    @ApiModelProperty(value = "接收者", example = "")
    private String receiver;

    /**
     * 消息体
     */
    @ApiModelProperty(value = "消息体", example = "")
    private String content;

    /**
     * 接收时间
     */
    @ApiModelProperty(value = "接收时间", example = "")
    private String recvTime;

    /**
     * 状态：未处理、处理中、已处理
     */
    @ApiModelProperty(value = "状态：未处理、处理中、已处理", example = "0")
    private Integer status;

    @TableField(exist = false)
    @ApiModelProperty(value = "开始时间", example = "")
    private String startTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "结束时间", example = "")
    private String endTime;
}