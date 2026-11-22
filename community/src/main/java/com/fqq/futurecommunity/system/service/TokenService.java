package com.fqq.futurecommunity.system.service;

/**
 * @author: fqq
 * @Date: 2026/4/11 - 04 - 11 - 23:17
 * @Description: com.fqq.futurecommunity.system.service
 * @version: 1.0
 */

import com.fqq.futurecommunity.system.domain.LoginUser;
import jakarta.servlet.http.HttpServletRequest;

/**
 * token验证处理
 * @author spikeCong
 * @date 2023/5/4
 **/
public interface TokenService {

    /**
     * 创建令牌
     * @param loginUser
     * @return: java.lang.String
     */
    String createToken(LoginUser loginUser);
    void refreshToken(LoginUser loginUser);
    LoginUser getLoginUser(HttpServletRequest request);
    void verifyToken(LoginUser loginUser);
    void setLoginUser(LoginUser loginUser);
    void delLoginUser(String token);


}
