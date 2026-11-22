package com.fqq.futurecommunity.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.system.domain.SysJobLog;

import java.util.List;

public interface SysJobLogService {

    Page<SysJobLog> selectJobLogPage(Page<SysJobLog> page, SysJobLog jobLog);

    List<SysJobLog> selectJobLogList(SysJobLog jobLog);

    int insertJobLog(SysJobLog jobLog);

    int deleteJobLogByIds(Long[] jobLogIds);

    int cleanJobLog();
}
