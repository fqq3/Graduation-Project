package com.fqq.futurecommunity.system.service.impl;

/**
 * @author: fqq
 * @Date: 2026/4/11 - 04 - 11 - 19:34
 * @Description: com.fqq.futurecommunity.system.service.impl
 * @version: 1.0
 */

import com.fqq.futurecommunity.common.core.exception.BaseException;
import com.fqq.futurecommunity.common.enums.UserStatus;
import com.fqq.futurecommunity.system.domain.LoginUser;
import com.fqq.futurecommunity.system.domain.SysUser;
import com.fqq.futurecommunity.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 用户验证处理
 * @author spikeCong
 * @date 2023/5/3
 **/
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService userService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SysUser user = userService.selectUserByUserName(username);

        if(Objects.isNull(user)){
            log.info("登录用户:{} 不存在",username);
            throw new UsernameNotFoundException("登录用户: " + username + " 不存在");
        }
        else if(UserStatus.DELETED.getCode().equals(user.getDelFlag())){
            log.info("登录用户:{} 已被删除",username);
            throw new BaseException("对不起,您的账号: " + username + " 以被删除" );
        }
        else if(UserStatus.DISABLE.getCode().equals(user.getStatus())){
            log.info("登录用户:{} 已被停用",username);
            throw new BaseException("对不起,您的账号: " + username + " 以被停用" );
        }

        return createLoginUser(user);
    }

    public UserDetails createLoginUser(SysUser user) {

        return new LoginUser(user);
    }
}

