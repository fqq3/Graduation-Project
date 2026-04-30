package com.fqq.community.common.core.domain;

/**
 * @author: fqq
 * @Date: 2026/3/6 - 03 - 06 - 16:09
 * @Description: com.fqq.community.common.core.domain
 * @version: 1.0
 */
public enum ResultCode {
    SUCCESS("200","操作成功"),
    ERROR("500","操作失败");
    private String code;//状态码
    private String message;//描述
    //构造方法
    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
