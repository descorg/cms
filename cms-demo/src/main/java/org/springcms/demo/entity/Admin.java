package org.springcms.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springcms.core.mybatis.base.CmsXBaseEntity;

/**
 * 系统账号
 * @TableName cms_admin
 */
@TableName(value ="cms_admin")
@Data
public class Admin extends CmsXBaseEntity {
    public enum AdminStatue {
        ADMIN_STATUE_NONE,
        ADMIN_STATUE_DISABLE,
        ADMIN_STATUE_ENABLE,
        ADMIN_STATUE_LOCK,
    }

    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "自增编号", example = "0")
    private Integer id;

    /**
     * 登录名
     */
    @ApiModelProperty(value = "登录名", example = "")
    private String name;

    /**
     * 登录密码
     */
    @ApiModelProperty(value = "登录密码", example = "")
    private String pass;

    /**
     * 电子邮箱
     */
    @ApiModelProperty(value = "电子邮箱", example = "")
    private String email;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话", example = "")
    private String phone;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称", example = "")
    private String nickname;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像", example = "")
    private String avatar;

    /**
     * 所属权限组
     */
    @ApiModelProperty(value = "所属权限组", example = "0")
    private Integer role;

    /**
     * 状态：1=禁用，2=启用，3=锁定
     */
    @ApiModelProperty(value = "状态：1=禁用，2=启用，3=锁定", example = "0")
    private Integer status;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    private Date time;

    /**
     * 登录次数
     */
    @ApiModelProperty(value = "登录次数", example = "0")
    private Integer loginNums;

    /**
     * 连续登陆失败次数
     */
    @ApiModelProperty(value = "连续登陆失败次数", example = "0")
    private Integer loginFail;

    /**
     * 最后登录时间
     */
    @ApiModelProperty(value = "最后登录时间", example = "")
    private Date lastLoginTime;

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
        Admin other = (Admin) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getPass() == null ? other.getPass() == null : this.getPass().equals(other.getPass()))
            && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
            && (this.getPhone() == null ? other.getPhone() == null : this.getPhone().equals(other.getPhone()))
            && (this.getNickname() == null ? other.getNickname() == null : this.getNickname().equals(other.getNickname()))
            && (this.getAvatar() == null ? other.getAvatar() == null : this.getAvatar().equals(other.getAvatar()))
            && (this.getRole() == null ? other.getRole() == null : this.getRole().equals(other.getRole()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getTime() == null ? other.getTime() == null : this.getTime().equals(other.getTime()))
            && (this.getLoginNums() == null ? other.getLoginNums() == null : this.getLoginNums().equals(other.getLoginNums()))
            && (this.getLoginFail() == null ? other.getLoginFail() == null : this.getLoginFail().equals(other.getLoginFail()))
            && (this.getLastLoginTime() == null ? other.getLastLoginTime() == null : this.getLastLoginTime().equals(other.getLastLoginTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getPass() == null) ? 0 : getPass().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getPhone() == null) ? 0 : getPhone().hashCode());
        result = prime * result + ((getNickname() == null) ? 0 : getNickname().hashCode());
        result = prime * result + ((getAvatar() == null) ? 0 : getAvatar().hashCode());
        result = prime * result + ((getRole() == null) ? 0 : getRole().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getTime() == null) ? 0 : getTime().hashCode());
        result = prime * result + ((getLoginNums() == null) ? 0 : getLoginNums().hashCode());
        result = prime * result + ((getLoginFail() == null) ? 0 : getLoginFail().hashCode());
        result = prime * result + ((getLastLoginTime() == null) ? 0 : getLastLoginTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", pass=").append(pass);
        sb.append(", email=").append(email);
        sb.append(", phone=").append(phone);
        sb.append(", nickname=").append(nickname);
        sb.append(", avatar=").append(avatar);
        sb.append(", role=").append(role);
        sb.append(", status=").append(status);
        sb.append(", time=").append(time);
        sb.append(", loginNums=").append(loginNums);
        sb.append(", loginFail=").append(loginFail);
        sb.append(", lastLoginTime=").append(lastLoginTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}