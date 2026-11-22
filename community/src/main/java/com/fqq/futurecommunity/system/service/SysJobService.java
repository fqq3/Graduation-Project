package com.fqq.futurecommunity.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.system.domain.SysJob;

import java.util.List;

public interface SysJobService {

    Page<SysJob> selectJobPage(Page<SysJob> page, SysJob job);

    List<SysJob> selectJobList(SysJob job);

    SysJob selectJobById(Long jobId);

    int insertJob(SysJob job);

    int updateJob(SysJob job);

    int deleteJobByIds(Long[] jobIds);

    int changeStatus(SysJob job);

    int run(SysJob job);
}
