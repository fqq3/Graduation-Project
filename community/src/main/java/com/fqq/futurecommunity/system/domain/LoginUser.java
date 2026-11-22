package com.fqq.futurecommunity.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * @author: fqq
 * @Date: 2026/4/11 - 04 - 11 - 19:57
 * @Description: com.fqq.futurecommunity.system.domain
 * @version: 1.0
 * 登录用户 身份权限对象
 * @author spikeCong
 * @date 2023/5/3
 **/
public class LoginUser implements UserDetails {

    /** * 用户唯一标识 */
    private String token;
    /** * 用户信息 */
    private SysUser user;
    /** * 登录时间 */
    private Long loginTime;
    /** * 过期时间 */
    private Long expireTime;
    /** * 权限列表 */
    private Set<String> permissions;



    //构造器
    //一定要有空参构造,否则序列化会失败
    public LoginUser() { }
    public LoginUser(SysUser user, Set<String> permissions) {
        this.user = user; this.permissions = permissions;
    }
    public LoginUser(SysUser user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    /**
     *  用于获取用户被授予的权限，可以用于实现访问控制。
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /**
     * 用于获取用户的密码，一般用于进行密码验证。
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 用于获取用户的用户名，一般用于进行身份验证。
     */
    @JsonIgnore
    @Override
    public String getUsername() {
        return user.getUserName(); // 注意这里是 userName 不是 username
    }
    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    /**
     * 用于判断用户的账户是否未过期，可以用于实现账户有效期控制。
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 用于判断用户的账户是否未锁定，可以用于实现账户锁定功能。
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 用于判断用户的凭证（如密码）是否未过期，可以用于实现密码有效期控制。
     */
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 用于判断用户是否已激活，可以用于实现账户激活功能。
     */
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}

