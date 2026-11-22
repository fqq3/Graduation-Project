package com.fqq.futurecommunity.common.core.exception;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.formula.functions.T;


/**
 * @author: fqq
 * @Date: 2026/4/11 - 04 - 11 - 23:42
 * @Description: com.fqq.futurecommunity.common.core.exception
 * @version: 1.0
 * 业务异常
 **/
/**
 * 业务异常
 * @author spikeCong
 * @date 2023/5/5
 **/
public class CustomException extends RuntimeException {

    /**
     * 状态码
     */
    private int code;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 承载数据
     */
    private T data;

    /**
     * 返回消息
     */
    private String msg;

    public CustomException() {
    }

    public CustomException(int code,String msg) {
        this.code = code;
        this.msg = msg;
        this.success = HttpServletResponse.SC_OK == code;
    }

    public CustomException(int code, boolean success, T data, String msg) {
        this.code = code;
        this.success = success;
        this.data = data;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
