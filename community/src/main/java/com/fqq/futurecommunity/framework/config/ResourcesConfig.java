package com.fqq.futurecommunity.framework.config;

import com.fqq.futurecommunity.common.constant.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * @author: fqq
 * @Date: 2026/4/13 - 04 - 13 - 22:28
 * @Description: com.fqq.futurecommunity.framework.config
 * @version: 1.0
 * 通用配置
 **/
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {

    /**
     * 跨域配置
     */
    @Bean
    public CorsFilter corsFilter()
    {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 设置访问源地址
        config.addAllowedOriginPattern("*");
        // 设置访问源请求头
        config.addAllowedHeader("*");
        // 设置访问源请求方法
        config.addAllowedMethod("*");
        // 对接口配置跨域设置
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String profileLocation = Paths.get(System.getProperty("user.dir"),
                        Constants.RESOURCE_PREFIX.replaceFirst("^/", ""))
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();
        registry.addResourceHandler(Constants.RESOURCE_PREFIX + "/**")
                .addResourceLocations(profileLocation.endsWith("/") ? profileLocation : profileLocation + "/");
    }


//    @Bean
//    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//
//        config.setAllowCredentials(true);
//
//        // 关键：不要用 addAllowedOrigin("*")
//        config.addAllowedOriginPattern("*");
//
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }
}
