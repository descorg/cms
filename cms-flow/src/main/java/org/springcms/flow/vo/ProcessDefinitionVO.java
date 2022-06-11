package org.springcms.flow.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("已部署的流程定义")
public class ProcessDefinitionVO {

    @ApiModelProperty(value = "模型主键", example = "")
    private String id;

    @ApiModelProperty(value = "模型标识", example = "")
    private String key;

    @ApiModelProperty(value = "模型名称", example = "")
    private String name;

    @ApiModelProperty(value = "描述", example = "")
    private String description;

    @ApiModelProperty(value = "版本号", example = "")
    private String version;

    @ApiModelProperty(value = "deploymentId", example = "")
    private String deploymentId;

    @ApiModelProperty(value = "流程图", example = "")
    private String images;
}
