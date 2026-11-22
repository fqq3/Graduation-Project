package com.fqq.futurecommunity.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.system.domain.SysJobLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {

    Page<SysJobLog> selectJobLogPage(Page<SysJobLog> page, @Param("jobLog") SysJobLog jobLog);

    List<SysJobLog> selectJobLogList(@Param("jobLog") SysJobLog jobLog);

    int cleanJobLog();
}
