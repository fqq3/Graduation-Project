package com.fqq.futurecommunity.system.service.impl;

import com.fqq.futurecommunity.common.core.exception.CaptchaNotMatchException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.fqq.futurecommunity.common.constant.Constants;
import com.fqq.futurecommunity.common.core.exception.BaseException;
import com.fqq.futurecommunity.common.utils.RedisCache;
import com.fqq.futurecommunity.system.domain.LoginUser;
import com.fqq.futurecommunity.system.service.SysLoginService;
import com.fqq.futurecommunity.system.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * @author: fqq
 * @Date: 2026/4/11 - 04 - 11 - 23:04
 * @Description: com.fqq.futurecommunity.system.service.impl
 * @version: 1.0
 * 登录校验
 * @author spikeCong
 * @date 2023/5/4
 **/

@Component
public class SysLoginServiceImpl implements SysLoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private TokenService tokenService;
    /**
     * 带验证码登录
     * @param username
     * @param password
     * @param code
     * @param uuid
     * @return: java.lang.String
     */
    @Override
    public String login(String username, String password, String code, String uuid) {

        //1.从redis中获取验证码,判断是否正确
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = redisCache.getCacheObject(verifyKey);
//        System.out.println("输入验证码: " + code);//debug
//        System.out.println("Redis验证码: " + captcha);//
//        System.out.println("uuid: " + uuid);//
        redisCache.deleteObject(verifyKey);

        if (captcha == null || !code.equalsIgnoreCase(captcha)){
            throw new CaptchaNotMatchException();//"验证码错误!"
        }

        //2.进行用户认证
        Authentication authentication = null;
        try {
            //该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        }catch (Exception e){
            throw new BaseException("用户不存在或密码错误！");
        }

        //3. 获取经过身份验证的用户的主体信息
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        //4.调用TokenService 生成token
        return tokenService.createToken(loginUser);
    }
}

