package org.springcms.core.mybatis.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("分页查询")
public class Query {

    @ApiModelProperty(value = "当前页码", example = "1")
    private Integer current;

    @ApiModelProperty(value = "每页显示记录条数", example = "10")
    private Integer size;

    public Integer getCurrent() {
        return this.current == null || this.current < 1 ? 1 : this.current;
    }

    public Integer getSize() {
        return this.size == null || this.size < 1 ? 10 : this.size;
    }
}
