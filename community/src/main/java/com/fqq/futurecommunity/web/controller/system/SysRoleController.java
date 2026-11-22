package com.fqq.futurecommunity.web.controller.system;


import com.fqq.futurecommunity.common.constant.UserConstants;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.common.utils.ServletUtils;
import com.fqq.futurecommunity.framework.service.SysPermissionService;
import com.fqq.futurecommunity.system.domain.LoginUser;
import com.fqq.futurecommunity.system.domain.SysRole;
import com.fqq.futurecommunity.system.service.SysRoleService;
import com.fqq.futurecommunity.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.fqq.futurecommunity.system.service.TokenService;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * @author spikeCong
 * @date 2023/5/26
 **/
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController {

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private SysUserService userService;

    @RequestMapping("/list")
    @PreAuthorize("@pe.hasPerms('system:role:list')")
    public PageResult list(SysRole role){
        startPage();
        List<SysRole> sysRoles = roleService.selectRoleList(role);
        return getData(sysRoles);
    }

    @GetMapping(value = "/{roleId}")
    @PreAuthorize("@pe.hasPerms('system:role:query')")
    public BaseResponse getInfo(@PathVariable Long roleId){
        return BaseResponse.success(roleService.selectRoleById(roleId));
    }

    @PutMapping("/changeStatus")
    @PreAuthorize("@pe.hasPerms('system:role:edit')")
    public BaseResponse changeStatus(@RequestBody SysRole role){
        roleService.checkRoleAllowed(role);
        role.setUpdateBy(SecurityUtils.getUserName());
        return toAjax(roleService.updateRoleStatus(role));
    }

    @DeleteMapping("/{roleIds}")
    @PreAuthorize("@pe.hasPerms('system:role:remove')")
    public BaseResponse remove(@PathVariable Long[] roleIds){
        return toAjax(roleService.deleteRoleByIds(roleIds));
    }

    //获取角色选择框列表
    @GetMapping("/optionselect")
    @PreAuthorize("@pe.hasPerms('system:role:query')")
    public BaseResponse optionSelect(){
        return BaseResponse.success(roleService.selectRoleAll());
    }


    @PostMapping
    @PreAuthorize("@pe.hasPerms('system:role:add')")
    public BaseResponse add(@RequestBody SysRole role){
        if(UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))){
            return BaseResponse.fail("新增角色" + role.getRoleName() + "失败,角色名称已存在");
        }
        else if(UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))){
            return BaseResponse.fail("新增角色" + role.getRoleKey() + "失败,角色权限已存在");
        }

        role.setCreateBy(SecurityUtils.getUserName());
        return toAjax(roleService.insertRole(role));
    }

    /**
     * 修改角色
     */
    @PutMapping
    @PreAuthorize("@pe.hasPerms('system:role:edit')")
    public BaseResponse edit(@RequestBody SysRole role){

        roleService.checkRoleAllowed(role);
        if(UserConstants.NOT_UNIQUE.equals(roleService.checkRoleNameUnique(role))){
            return BaseResponse.fail("修改角色" + role.getRoleName() + "失败,角色名称已存在");
        }
        else if(UserConstants.NOT_UNIQUE.equals(roleService.checkRoleKeyUnique(role))){
            return BaseResponse.fail("修改角色" + role.getRoleKey() + "失败,角色权限已存在");
        }

        role.setUpdateBy(SecurityUtils.getUserName());

        if(roleService.updateRole(role) > 0){
            //更新用户缓存
            LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
            if(!Objects.isNull(loginUser) && !loginUser.getUser().isAdmin()){
                //获取当前用户菜单权限
                loginUser.setPermissions(permissionService.getMenuPermission(loginUser.getUser()));
                loginUser.setUser(userService.selectUserByUserName(loginUser.getUser().getUserName()));
                tokenService.setLoginUser(loginUser);
            }

            return BaseResponse.success("修改角色成功");
        }

        return BaseResponse.fail("修改角色" + role.getRoleName() + "失败,请联系管理员");
    }

    @PutMapping("/dataScope")
    @PreAuthorize("@pe.hasPerms('system:role:edit')")
    public BaseResponse dataScope(@RequestBody SysRole role) {
        roleService.checkRoleAllowed(role);
        role.setUpdateBy(SecurityUtils.getUserName());
        return toAjax(roleService.authDataScope(role));
    }

    @GetMapping("/export")
    @PreAuthorize("@pe.hasPerms('system:role:export')")
    public ChainedMap export(SysRole role) {
        List<SysRole> roleList = roleService.selectRoleList(role);
        String fileName = FileStorageUtils.generateFileName("role", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(roleList, SysRole.class, filePath, "Role");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }
}
