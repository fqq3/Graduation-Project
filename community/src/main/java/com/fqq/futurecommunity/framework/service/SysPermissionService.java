package com.fqq.futurecommunity.framework.service;

import com.fqq.futurecommunity.system.domain.SysUser;
import com.fqq.futurecommunity.system.service.SysMenuService;
import com.fqq.futurecommunity.system.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: fqq
 * @Date: 2026/4/14 - 04 - 14 - 20:35
 * @Description: com.fqq.futurecommunity.framework.service
 * @version: 1.0
 * 用户权限处理
 **/
@Component
public class SysPermissionService {

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysMenuService menuService;

    /**
     * 获取角色数据权限
     * @param user
     * @return: 角色权限信息
     */
    public Set<String> getRolePermission(SysUser user){

        Set<String> roles = new HashSet<>();
        //管理员拥有所有权限
        if(user.isAdmin()){
            roles.add("admin");
        }else{
            roles = roleService.selectRolePermissionByUserId(user.getUserId());
        }

        return roles;
    }

    /**
     * 获取菜单数据权限
     * @param user
     * @return: java.util.Set<java.lang.String>
     */
    public Set<String> getMenuPermission(SysUser user){
        Set<String> perms = new HashSet<>();
        //管理员拥有所有权限
        if(user.isAdmin()){
            perms.add("*:*:*");
        }else{
            perms = menuService.selectMenuPermsByUserId(user.getUserId());
        }
        return perms;
    }
}

