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
 * @TableName cms_mq_producer
 */
@TableName(value ="cms_mq_producer")
@Data
@ApiModel("生产者对象")
public class MqProducer implements Serializable {
    /**
     * 消息状态
     */
    public enum QueueStatue {
        /**
         * 未发送，发送失败
         */
        QUEUE_STATUE_NOTSENT,
        /**
         * 发送中
         */
        QUEUE_STATUE_SENDING,
        /**
         * 已发送
         */
        QUEUE_STATUE_SENDED,
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
     * 消息体
     */
    @ApiModelProperty(value = "消息体", example = "")
    private String content;

    /**
     * 发送时间
     */
    @ApiModelProperty(value = "发送时间", example = "")
    private String sendTime;

    /**
     * 状态：未发送、发送中、已发送
     */
    @ApiModelProperty(value = "状态：未发送、发送中、已发送", example = "0")
    private Integer status;

    @TableField(exist = false)
    @ApiModelProperty(value = "开始时间", example = "")
    private String startTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "结束时间", example = "")
    private String endTime;
}