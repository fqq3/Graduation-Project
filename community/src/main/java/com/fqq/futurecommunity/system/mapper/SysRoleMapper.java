package com.fqq.futurecommunity.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fqq.futurecommunity.system.domain.SysRole;

import java.util.List;

public interface SysRoleMapper extends BaseMapper<SysRole> {

    List<String> selectRolePermissionByUserId(Long userId);

    List<SysRole> selectRoleList(SysRole sysRole);

    List<Integer> selectRoleListByUserId(Long userId);

    SysRole selectRoleById(Long roleId);

    List<SysRole> selectRoleAll();

    List<SysRole> selectRolesByUserName(String userName);

    SysRole checkRoleNameUnique(String roleName);

    SysRole checkRoleKeyUnique(String roleKey);

    int updateRole(SysRole role);

    int insertRole(SysRole role);

    int deleteRoleById(Long roleId);

    int deleteRoleByIds(Long[] roleIds);
}
