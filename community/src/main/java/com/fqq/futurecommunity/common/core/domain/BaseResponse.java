package com.fqq.community.common.core.domain;

import java.io.Serializable;

/**
 * @author: fqq
 * @Date: 2026/3/6 - 03 - 06 - 16:30
 * @Description: com.fqq.community.common.core.domain
 * @version: 1.0
 */
public class BaseResponse <T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private String code;//响应状态码
    private String message;//响应
    private T data;//返回的数据

    /** * 成功返回 * @param data * @return: community.common.core.domain.BaseResponse<T> */
    public static <T> BaseResponse<T> success(T data){
        BaseResponse<T> response = new BaseResponse<>();
        response.setCode(ResultCode.SUCCESS.getCode());
        response.setMessage(ResultCode.SUCCESS.getMessage());
        response.setData(data); return response; }

    /** * 失败返回 * @param message * @return: community.common.core.domain.BaseResponse<T> */
    public static <T> BaseResponse<T> fail(String message){
        BaseResponse<T> response = new BaseResponse<>();
        response.setCode(ResultCode.ERROR.getCode());
        response.setMessage(message); return response;
    }
    /** * 失败返回 * @param message * @return: community.common.core.domain.BaseResponse<T> */
    public static <T> BaseResponse<T> fail(String code, String message){
        BaseResponse<T> response = new BaseResponse<>();
        response.setCode(code); response.setMessage(message);
        return response; } public String getCode() { return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
