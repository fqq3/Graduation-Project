package com.fqq.futurecommunity.framework.security.handle;

import com.alibaba.fastjson2.JSON;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.utils.ServletUtils;
import com.fqq.futurecommunity.system.domain.LoginUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import com.fqq.futurecommunity.system.service.TokenService;
import java.util.Objects;
import java.io.IOException;
/**
 * @author: fqq
 * @Date: 2026/4/18 - 04 - 18 - 2:26
 * @Description: com.fqq.futurecommunity.framework.security.handle
 * @version: 1.0
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Autowired
    private TokenService tokenService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {

        LoginUser loginUser = tokenService.getLoginUser(request);
        if(!Objects.isNull(loginUser)){
            //删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
        }

        ServletUtils.renderString(response, JSON.toJSONString(BaseResponse.success(null)));
    }
}