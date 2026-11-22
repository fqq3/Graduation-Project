package com.fqq.futurecommunity.framework.security.handle;

import com.alibaba.fastjson2.JSON;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.utils.ServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author: fqq
 * @Date: 2026/4/11 - 04 - 11 - 20:03
 * @Description: com.fqq.futurecommunity.framework.config.security
 * @version: 1.0
 */


/**
 * 认证失败处理类
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        Integer code = HttpStatus.UNAUTHORIZED.value();

        ServletUtils.renderString(
                response,
                JSON.toJSONString(BaseResponse.fail(code.toString(), "认证失败，无法访问系统资源"))
        );
    }
}