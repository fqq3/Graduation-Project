package com.fqq.futurecommunity.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.system.domain.SysOperLog;
import com.fqq.futurecommunity.system.mapper.SysOperLogMapper;
import com.fqq.futurecommunity.system.service.SysOperLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SysOperLogServiceImpl implements SysOperLogService {
    @Resource
    private SysOperLogMapper operLogMapper;

    @Override
    public Page<SysOperLog> selectOperLogList(Page<SysOperLog> page, SysOperLog operLog) {
        return operLogMapper.selectOperLogList(page, operLog);
    }

    @Override
    public int deleteOperLogByIds(Long[] operIds) {
        int rows = 0;
        for (Long operId : operIds) {
            rows += operLogMapper.deleteById(operId);
        }
        return rows;
    }

    @Override
    public int cleanOperLog() {
        return operLogMapper.cleanOperLog();
    }
}
