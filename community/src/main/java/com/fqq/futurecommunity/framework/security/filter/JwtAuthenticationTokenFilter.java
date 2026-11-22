package com.fqq.futurecommunity.framework.security.filter;

import com.fqq.futurecommunity.system.domain.LoginUser;
import com.fqq.futurecommunity.system.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Objects;


/**
 * @author: fqq
 * @Date: 2026/4/13 - 04 - 13 - 22:24
 * @Description: com.fqq.futurecommunity.framework.security.filter
 * @version: 1.0
 * token过滤器 验证token有效性
 **/

/**
 * token过滤器 验证token有效性
 **/
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private static final String[] EXCLUDE_PATHS = {
            "/login",
            "/captchaImage"
    };

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private TokenService tokenService;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath(); //

        for (String pattern : EXCLUDE_PATHS) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        //从Redis获取用户信息
        LoginUser loginUser = tokenService.getLoginUser(request);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //判断: loginUser不为空,authentication为空,用户持有token 需要验证
        if(!Objects.isNull(loginUser) && Objects.isNull(authentication)){
            tokenService.verifyToken(loginUser);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUser,null,loginUser.getAuthorities());
            //设置与当前身份验证相关的详细信息(远程IP地址、会话ID等)
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        }

        filterChain.doFilter(request,response);
    }
}
