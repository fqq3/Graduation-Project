package com.fqq.futurecommunity.common.utils;
import org.springframework.security.core.Authentication;
import com.fqq.futurecommunity.common.constant.HttpStatus;
import com.fqq.futurecommunity.common.core.exception.CustomException;
import com.fqq.futurecommunity.system.domain.LoginUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 用户信息相关操作工具类
 * @author spikeCong
 * @date 2023/5/22
 **/
public class SecurityUtils {

    /**
     * 获取用户账户
     */
    public static String getUserName(){
        try {
            return getLoginUser().getUsername();
        } catch (Exception e) {
            throw new CustomException(HttpStatus.UNAUTHORIZED,"获取用户账户异常");
        }
    }

    /**
     * 获取完整登录用户信息
     */
    public static LoginUser getLoginUser(){
        try {
            return  (LoginUser) getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new CustomException(HttpStatus.UNAUTHORIZED,"获取用户信息异常");
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication(){

        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 生成 BCryptPasswordEncoder 密码
     * @param password
     * @return: java.lang.String
     */
    public static String encryptPassword(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
}
