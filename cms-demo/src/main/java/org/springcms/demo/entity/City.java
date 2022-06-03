package org.springcms.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springcms.core.mybatis.base.CmsXBaseEntity;

/**
 * 行政区划表
 * @TableName cms_city
 */
@TableName(value ="cms_city")
@Data
public class City extends CmsXBaseEntity<Long> {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键", example = "0")
    private Long id;

    /**
     * 父级id
     */
    @ApiModelProperty(value = "父级id", example = "0")
    private Long pid;

    /**
     * 城市名称
     */
    @ApiModelProperty(value = "城市名称", example = "")
    private String name;

    /**
     * 地市简拼
     */
    @ApiModelProperty(value = "地市简拼", example = "")
    private String shortname;

    /**
     * 行政区划编码
     */
    @ApiModelProperty(value = "行政区划编码", example = "")
    private String code;

    /**
     * 区划等级 1-省 2-市 3-区
     */
    @ApiModelProperty(value = "区划等级 1-省 2-市 3-区", example = "0")
    private Integer level;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度", example = "")
    private String longitude;

    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度", example = "")
    private String latitude;

    /**
     * 1=禁用，2=启用
     */
    @ApiModelProperty(value = "状态：1=禁用，2=启用", example = "0")
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        City other = (City) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPid() == null ? other.getPid() == null : this.getPid().equals(other.getPid()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getShortname() == null ? other.getShortname() == null : this.getShortname().equals(other.getShortname()))
            && (this.getCode() == null ? other.getCode() == null : this.getCode().equals(other.getCode()))
            && (this.getLevel() == null ? other.getLevel() == null : this.getLevel().equals(other.getLevel()))
            && (this.getLongitude() == null ? other.getLongitude() == null : this.getLongitude().equals(other.getLongitude()))
            && (this.getLatitude() == null ? other.getLatitude() == null : this.getLatitude().equals(other.getLatitude()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPid() == null) ? 0 : getPid().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getShortname() == null) ? 0 : getShortname().hashCode());
        result = prime * result + ((getCode() == null) ? 0 : getCode().hashCode());
        result = prime * result + ((getLevel() == null) ? 0 : getLevel().hashCode());
        result = prime * result + ((getLongitude() == null) ? 0 : getLongitude().hashCode());
        result = prime * result + ((getLatitude() == null) ? 0 : getLatitude().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", pid=").append(pid);
        sb.append(", name=").append(name);
        sb.append(", shortname=").append(shortname);
        sb.append(", code=").append(code);
        sb.append(", level=").append(level);
        sb.append(", longitude=").append(longitude);
        sb.append(", latitude=").append(latitude);
        sb.append(", status=").append(status);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}