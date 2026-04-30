package com.fqq.community.web.test;

import jakarta.validation.constraints.NotNull;

/**
 * @author: fqq
 * @Date: 2026/3/6 - 03 - 06 - 17:09
 * @Description: com.fqq.community.web.test
 * @version: 1.0
 */
public class User {

    @NotNull(message = "用户id不能为空")//Spring-Validation提供
    private Integer userId;
    @NotNull(message = "用户名不能为空")
    private String username;
    public User() {
    }
    public User(Integer userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
