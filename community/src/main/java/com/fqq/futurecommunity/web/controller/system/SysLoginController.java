package com.fqq.futurecommunity.web.controller.system;

import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ServletUtils;
import com.fqq.futurecommunity.framework.service.SysPermissionService;
import com.fqq.futurecommunity.system.domain.LoginUser;
import com.fqq.futurecommunity.system.domain.SysMenu;
import com.fqq.futurecommunity.system.domain.SysUser;
import com.fqq.futurecommunity.system.domain.vo.LoginBody;
import com.fqq.futurecommunity.system.domain.vo.RouterVo;
import com.fqq.futurecommunity.system.service.SysLoginService;
import com.fqq.futurecommunity.system.service.SysMenuService;
import com.fqq.futurecommunity.system.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;


/**
 * @author: fqq
 * @Date: 2026/4/11 - 04 - 11 - 23:32
 * @Description: com.fqq.futurecommunity.web.controller.system
 * @version: 1.0
 * 登录验证
 **/
@RestController
public class SysLoginController {

    @Autowired
    private SysLoginService loginService;
    /** * 登录方法 * @param loginBody * @return: com.msb.hjycommunity.common.utils.ChainedMap */
    @PostMapping("/login")
    public ChainedMap login(@RequestBody LoginBody loginBody){
        //生成令牌
        String token = loginService.login(loginBody.getUserName(), loginBody.getPassword(), loginBody.getCode(), loginBody.getUuid());
        return ChainedMap.create().set("token",token);
    }


    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysMenuService sysMenuService;
    /**
     * 获取 用户信息
     * @param
     * @return: 用户信息
     */
    @GetMapping("/getInfo")
    public ChainedMap getInfo(){

        //用户信息
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        //角色集合
        Set<String> roles = permissionService.getRolePermission(user);

        //权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);

        ChainedMap map = ChainedMap.create().set("code", 200).set("msg", "操作成功");
        map.put("user",user);
        map.put("roles",roles);
        map.put("permissions",permissions);

        return map;
    }
//    /**
//     * 获取路由信息
//     * @param
//     * @return: 路由信息
//     */
//    @GetMapping("/getRouters")
//    public BaseResponse getRouters(){
//        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
//        SysUser user = loginUser.getUser();
//        List<SysMenu> menus = sysMenuService.selectMenuTreeByUserId(user.getUserId());
//
//        return BaseResponse.success(menus);
//    }
    /**
     * 获取路由信息
     * @param
     * @return: 路由信息
     */
    @GetMapping("/getRouters")
    public BaseResponse getRouters(){
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        //获取菜单列表
        List<SysMenu> menus = sysMenuService.selectMenuTreeByUserId(user.getUserId());
        //转换为前端需要的路由列表
        List<RouterVo> routerVoList = sysMenuService.buildMenus(menus);

        return BaseResponse.success(routerVoList);
    }


}


