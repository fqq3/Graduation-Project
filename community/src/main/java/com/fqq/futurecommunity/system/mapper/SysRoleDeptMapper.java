package com.fqq.futurecommunity.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fqq.futurecommunity.system.domain.SysRoleDept;

import java.util.List;

/**
 * Role and department relation mapper.
 */
public interface SysRoleDeptMapper extends BaseMapper<SysRoleDept> {

    int deleteRoleDeptByRoleId(Long roleId);

    int batchRoleDept(List<SysRoleDept> roleDeptList);
}
