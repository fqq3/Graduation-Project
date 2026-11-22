package com.fqq.futurecommunity.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.system.domain.SysLogininfor;
import com.fqq.futurecommunity.system.mapper.SysLogininforMapper;
import com.fqq.futurecommunity.system.service.SysLogininforService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SysLogininforServiceImpl implements SysLogininforService {
    @Resource
    private SysLogininforMapper logininforMapper;

    @Override
    public Page<SysLogininfor> selectLogininforList(Page<SysLogininfor> page, SysLogininfor logininfor) {
        return logininforMapper.selectLogininforList(page, logininfor);
    }

    @Override
    public int deleteLogininforByIds(Long[] infoIds) {
        int rows = 0;
        for (Long infoId : infoIds) {
            rows += logininforMapper.deleteById(infoId);
        }
        return rows;
    }

    @Override
    public int cleanLogininfor() {
        return logininforMapper.cleanLogininfor();
    }
}
