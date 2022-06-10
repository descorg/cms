package org.springcms.flow.vo;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("流程事项")
public class ProcessVO {

    @ApiModelProperty(value = "类别：报销=Expense，请假=Leave", example = "Leave")
    private String category;

    @ApiModelProperty(value = "具体信息：如请假起止时间；报销费用及附件", example = "")
    private JSONObject data;

    @ApiModelProperty(value = "描述", example = "")
    private String descption;
}
