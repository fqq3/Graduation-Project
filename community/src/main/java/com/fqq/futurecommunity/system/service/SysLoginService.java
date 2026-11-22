package com.fqq.futurecommunity.system.service;

/**
 * @author: fqq
 * @Date: 2026/4/11 - 04 - 11 - 23:01
 * @Description: com.fqq.futurecommunity.system.service
 * @version: 1.0
 */
public interface SysLoginService {

    public String login(String username, String password, String code, String uuid);
}

