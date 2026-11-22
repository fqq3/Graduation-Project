package com.fqq.futurecommunity;

import com.fqq.futurecommunity.system.mapper.SysRoleMapper;
import com.fqq.futurecommunity.system.mapper.SysUserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.fqq.futurecommunity.community.mapper",
		"com.fqq.futurecommunity.system.mapper"})
public class CommunityApplication {


	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
		System.out.println("社区后台管理系统启动成功！");
	}

}
