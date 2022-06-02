package org.springcms.core.utils;

import lombok.Data;

@Data
public class ResponseUtils<T> {
    private long code;
    private String message;
    private T data;

    private static final Integer SUCCESS_CODE = 200;
    private static final Integer FAILED_CODE = 500;

    private static final String SUCCESS_MESSAGE = "success";

    public ResponseUtils() {

    }

    public ResponseUtils(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseUtils(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     */
    public static <T> ResponseUtils<T> success() {
        return new ResponseUtils<T>(SUCCESS_CODE, SUCCESS_MESSAGE, null);
    }

    /**
     * 成功返回结果
     */
    public static <T> ResponseUtils<T> success(String message) {
        return new ResponseUtils<T>(SUCCESS_CODE, message, null);
    }

    /**
     * 成功返回结果
     * @param data 获取的数据
     */
    public static <T> ResponseUtils<T> success(T data) {
        return new ResponseUtils<T>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    /**
     * 成功返回结果
     * @param  message 提示信息
     * @param data 获取的数据
     */
    public static <T> ResponseUtils<T> success(String message, T data) {
        return new ResponseUtils<T>(SUCCESS_CODE, message, data);
    }

    /**
     * 失败返回结果
     * @param errorCode 异常
     */
    public static <T> ResponseUtils<T> failed(Exception errorCode) {
        return new ResponseUtils<T>(FAILED_CODE, errorCode.getMessage(), null);
    }

    /**
     * 失败返回结果
     * @param error 错误信息
     * @param <T>
     * @return
     */
    public static <T> ResponseUtils<T> failed(String error) {
        return new ResponseUtils<T>(FAILED_CODE, error, null);
    }

    /**
     * 失败返回结果
     * @param code 错误码
     * @param error 错误信息
     * @param <T>
     * @return
     */
    public static <T> ResponseUtils<T> failed(Integer code, String error) {
        return new ResponseUtils<T>(code, error, null);
    }
}
