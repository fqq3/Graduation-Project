package com.fqq.futurecommunity.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.system.domain.SysLogininfor;

public interface SysLogininforService {
    Page<SysLogininfor> selectLogininforList(Page<SysLogininfor> page, SysLogininfor logininfor);
    int deleteLogininforByIds(Long[] infoIds);
    int cleanLogininfor();
}
