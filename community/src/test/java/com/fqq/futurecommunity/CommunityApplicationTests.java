package com.fqq.futurecommunity;

import com.fqq.futurecommunity.system.domain.SysMenu;
import com.fqq.futurecommunity.system.domain.SysUser;
import com.fqq.futurecommunity.system.mapper.SysMenuMapper;
import com.fqq.futurecommunity.system.mapper.SysRoleMapper;
import com.fqq.futurecommunity.system.mapper.SysUserMapper;
import com.fqq.futurecommunity.system.service.SysMenuService;
import com.fqq.futurecommunity.system.service.SysRoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLOutput;
import java.util.List;
import java.util.Set;

@SpringBootTest
class CommunityApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	SysUserMapper userMapper;
	@Autowired
	SysUserMapper sysUserMapper;
	@Autowired
	SysRoleService sysRoleService;
	@Autowired
	SysMenuService sysMenuService;
	@Autowired
	SysMenuMapper sysMenuMapper;
	@Test
	public void testSelectMenuTreeAll(){
		List<SysMenu> sysMenus=sysMenuMapper.selectMenuTreeAll();
		System.out.println(sysMenus);
	}

	@Test
	public void testSelectRoleAndMenuByUserId(){
		Set<String> s1=sysRoleService.selectRolePermissionByUserId(1L);
		System.out.println("用户角色权限信息"+s1);
		Set<String> s2=sysMenuService.selectMenuPermsByUserId(2L);
		System.out.println("用户菜单权限信息"+s2);
	}

	@Test
	public void testSelectUserByUserName(){
		SysUser admin = userMapper.selectUserByUserName("admin");
		System.out.println(admin);
	}




}
