package org.springcms.flow.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("任务")
public class TaskVO {

    @ApiModelProperty(value = "任务编号", example = "")
    private String id;

    @ApiModelProperty(value = "当前步骤", example = "")
    private String name;

    @ApiModelProperty(value = "任务标识", example = "")
    private String taskDefinitionKey;

    @ApiModelProperty(value = "执行编号", example = "")
    private String assignee;

    @ApiModelProperty(value = "发起时间", example = "")
    private Date createTime;

    @ApiModelProperty(value = "执行编号", example = "")
    private String executionId;

    @ApiModelProperty(value = "流程编号", example = "")
    private String processInstanceId;

    @ApiModelProperty(value = "processDefinitionId", example = "")
    private String processDefinitionId;

    @ApiModelProperty(value = "流程主键", example = "")
    private String processDefinitionKey;

    @ApiModelProperty(value = "流程名称", example = "")
    private String processDefinitionName;

    @ApiModelProperty(value = "版本号", example = "1")
    private Integer revision;
}
