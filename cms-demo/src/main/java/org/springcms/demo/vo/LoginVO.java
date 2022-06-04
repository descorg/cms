package org.springcms.demo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("当前登录用户信息")
public class LoginVO {
    @ApiModelProperty(value = "用户编号", example = "0")
    private Integer id;

    @ApiModelProperty(value = "用户名", example = "")
    private String name;

    @ApiModelProperty(value = "授权码", example = "")
    private String token;
}
