package com.fqq.futurecommunity.framework.config;

import com.fqq.futurecommunity.framework.security.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    /**
     * 认证失败处理器
     */
    @Autowired
    private AuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtAuthenticationTokenFilter authenticationTokenFilter;

    @Autowired
    private CorsFilter corsFilter;

    @Autowired
    private org.springframework.security.web.authentication.logout.LogoutSuccessHandler logoutSuccessHandler;

    /**
     * AuthenticationManager（替代旧版 authenticationManagerBean）
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 核心安全配置（替代 configure(HttpSecurity)）
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // CSRF禁用
                .csrf(csrf -> csrf.disable())

                // 允许 Druid / Swagger / 外链页面在前端 iframe 中展示。
                // 当前前端的监控与系统接口页面通过 iframe 加载这些地址，
                // Spring Security 默认 X-Frame-Options: DENY 会导致浏览器拒绝显示。
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                )

                // 不使用session（JWT）
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )


                .authorizeHttpRequests(auth -> auth
                        // 未登录才能访问（登录页、验证码）
                        .requestMatchers(
                                "/login",
                                "/captchaImage"
                        ).permitAll()

                        .requestMatchers(
                                "/druid/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/doc.html",
                                "/webjars/**"
                        ).permitAll()
                        // 其他请求必须登录
                        .anyRequest().authenticated()
                )

                // 登出（新版写法）
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                )

                // 异常处理
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(unauthorizedHandler)
                );

        // ===== 过滤器顺序（完全保留你原来的逻辑） =====

        // JWT过滤器
        http.addFilterBefore(authenticationTokenFilter,
                UsernamePasswordAuthenticationFilter.class);

        // CORS过滤器（保持你原来的两个位置）
        http.addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class);
        http.addFilterBefore(corsFilter, LogoutFilter.class);

        return http.build();
    }

    /**
     * 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}
