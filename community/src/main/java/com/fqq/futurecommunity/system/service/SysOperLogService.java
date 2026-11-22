package com.fqq.futurecommunity.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.system.domain.SysOperLog;

public interface SysOperLogService {
    Page<SysOperLog> selectOperLogList(Page<SysOperLog> page, SysOperLog operLog);
    int deleteOperLogByIds(Long[] operIds);
    int cleanOperLog();
}
