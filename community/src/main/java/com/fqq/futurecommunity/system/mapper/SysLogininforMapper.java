package com.fqq.futurecommunity.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.system.domain.SysLogininfor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysLogininforMapper extends BaseMapper<SysLogininfor> {
    Page<SysLogininfor> selectLogininforList(Page<SysLogininfor> page, @Param("logininfor") SysLogininfor logininfor);
    int cleanLogininfor();
}
