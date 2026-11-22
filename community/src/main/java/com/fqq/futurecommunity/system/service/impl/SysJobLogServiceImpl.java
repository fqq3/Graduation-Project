package com.fqq.futurecommunity.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.system.domain.SysJobLog;
import com.fqq.futurecommunity.system.mapper.SysJobLogMapper;
import com.fqq.futurecommunity.system.service.SysJobLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysJobLogServiceImpl implements SysJobLogService {

    @Resource
    private SysJobLogMapper jobLogMapper;

    @Override
    public Page<SysJobLog> selectJobLogPage(Page<SysJobLog> page, SysJobLog jobLog) {
        return jobLogMapper.selectJobLogPage(page, jobLog);
    }

    @Override
    public List<SysJobLog> selectJobLogList(SysJobLog jobLog) {
        return jobLogMapper.selectJobLogList(jobLog);
    }

    @Override
    public int insertJobLog(SysJobLog jobLog) {
        return jobLogMapper.insert(jobLog);
    }

    @Override
    public int deleteJobLogByIds(Long[] jobLogIds) {
        int rows = 0;
        for (Long jobLogId : jobLogIds) {
            rows += jobLogMapper.deleteById(jobLogId);
        }
        return rows;
    }

    @Override
    public int cleanJobLog() {
        return jobLogMapper.cleanJobLog();
    }
}
