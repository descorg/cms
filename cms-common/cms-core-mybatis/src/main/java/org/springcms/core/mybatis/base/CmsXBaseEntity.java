package org.springcms.core.mybatis.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 *
 * @param <K> 自增字段类型
 */
@Data
public class CmsXBaseEntity<K> {

    @ApiModelProperty(value = "主键", example = "0")
    @TableId(value = "id", type = IdType.AUTO)
    private K id;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:ii:ss")
    @ApiModelProperty(value = "创建时间", example = "")
    private Date createTime;

    @TableField(exist = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:ii:ss")
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @TableField(exist = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:ii:ss")
    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
