package com.fqq.futurecommunity.common.enums;

/**
 * @author: fqq
 * @Date: 2026/4/11 - 04 - 11 - 19:54
 * @Description: com.fqq.futurecommunity.common.enums
 * @version: 1.0
 * 用户状态
 **/
public enum UserStatus {

    OK("0","正常"),DISABLE("1","停用"),DELETED("2","删除");

    private final String code;
    private final String info;

    UserStatus(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }
    public String getInfo() {
        return info;
    }
}
