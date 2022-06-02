package org.springcms.core.mybatis.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "返回信息")
public class R<T> implements Serializable {

    @ApiModelProperty(value = "状态码", required = true)
    private Integer code;

    @ApiModelProperty(value = "返回消息")
    private String msg;

    @ApiModelProperty(value = "数据体")
    private T data;

    public static <T> R<T> data(T data) {
        return data(data, "操作成功");
    }

    public R(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private R(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static <T> R<T> data(T data, String msg) {
        return data(200, data, msg);
    }

    public static <T> R<T> data(int code, T data, String msg) {
        return new R<>(code, data, (data == null) ? "": msg);
    }

    public static <T> R<T> success(String msg) {
        return new R<>(200, msg);
    }

    public static <T> R<T> success(Integer code, String msg) {
        return new R<>(code, msg);
    }

    public static <T> R<T> fail(String msg) {
        return new R<>(400, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, null, msg);
    }

    public String toString() {
        return "R(code=" + getCode() + ", data=" + getData() + ", msg=" + getMsg() + ")";
    }
}
