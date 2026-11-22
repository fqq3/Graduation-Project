package com.fqq.futurecommunity.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.system.domain.SysOperLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
    Page<SysOperLog> selectOperLogList(Page<SysOperLog> page, @Param("operLog") SysOperLog operLog);
    int cleanOperLog();
}
