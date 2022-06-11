package org.springcms.flow.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("流程详情")
public class ProcessDetailVO {

    @ApiModelProperty(value = "流程编号", example = "")
    private String id;

    @ApiModelProperty(value = "发起者", example = "")
    private String startUserId;

    @ApiModelProperty(value = "发起时间", example = "")
    private Date startTime;

    @ApiModelProperty(value = "流程图", example = "")
    private String images;
}
