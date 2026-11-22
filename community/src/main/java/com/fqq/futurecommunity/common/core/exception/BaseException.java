package com.fqq.futurecommunity.common.core.exception;

/**
 * @author: fqq
 * @Date: 2026/3/7 - 03 - 07 - 2:13
 * @Description: com.fqq.community.common.core.exception
 * @version: 1.0
 */
public class BaseException extends RuntimeException{
    /** * 错误码 */
    private String code;
    /** * 错误消息 */
    private String defaultMessage;

    public BaseException() { }

    public BaseException(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public BaseException(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
    public String getCode() {

        return code;
    }
    public String getDefaultMessage() {

        return defaultMessage;
    }
}
