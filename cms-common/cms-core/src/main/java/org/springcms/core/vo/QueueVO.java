package org.springcms.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="QueueVO", description="消息类型")
public class QueueVO {
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

    @ApiModelProperty(value = "唯一标识", required = true)
    private String uuid;

    @ApiModelProperty(value = "消息来源", required = true)
    private String source;

    @ApiModelProperty(value = "消息正文", required = true)
    private String content;

    @ApiModelProperty(value = "发送时间", required = true)
    private String sendTime;

    @ApiModelProperty(value = "发送状态", required = true)
    private int statue;
}
