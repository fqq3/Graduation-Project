package com.fqq.futurecommunity.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.constant.UserConstants;
import com.fqq.futurecommunity.common.core.exception.CustomException;
import com.fqq.futurecommunity.system.domain.SysJob;
import com.fqq.futurecommunity.system.domain.SysJobLog;
import com.fqq.futurecommunity.system.mapper.SysJobLogMapper;
import com.fqq.futurecommunity.system.mapper.SysJobMapper;
import com.fqq.futurecommunity.system.service.SysJobService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class SysJobServiceImpl implements SysJobService {

    @Resource
    private SysJobMapper jobMapper;

    @Resource
    private SysJobLogMapper jobLogMapper;

    @Override
    public Page<SysJob> selectJobPage(Page<SysJob> page, SysJob job) {
        Page<SysJob> result = jobMapper.selectJobPage(page, job);
        enrichNextValidTime(result.getRecords());
        return result;
    }

    @Override
    public List<SysJob> selectJobList(SysJob job) {
        List<SysJob> jobs = jobMapper.selectJobList(job);
        enrichNextValidTime(jobs);
        return jobs;
    }

    @Override
    public SysJob selectJobById(Long jobId) {
        SysJob job = jobMapper.selectById(jobId);
        enrichNextValidTime(job);
        return job;
    }

    @Override
    public int insertJob(SysJob job) {
        validateCronExpression(job.getCronExpression());
        return jobMapper.insert(job);
    }

    @Override
    public int updateJob(SysJob job) {
        validateCronExpression(job.getCronExpression());
        return jobMapper.updateById(job);
    }

    @Override
    public int deleteJobByIds(Long[] jobIds) {
        int rows = 0;
        for (Long jobId : jobIds) {
            rows += jobMapper.deleteById(jobId);
        }
        return rows;
    }

    @Override
    public int changeStatus(SysJob job) {
        if (job.getJobId() == null) {
            return 0;
        }
        SysJob update = new SysJob();
        update.setJobId(job.getJobId());
        update.setStatus(job.getStatus());
        return jobMapper.updateById(update);
    }

    @Override
    public int run(SysJob job) {
        if (job.getJobId() == null) {
            return 0;
        }
        SysJob currentJob = jobMapper.selectById(job.getJobId());
        if (currentJob == null) {
            throw new CustomException(500, "Job not found");
        }

        SysJobLog jobLog = new SysJobLog();
        jobLog.setJobName(currentJob.getJobName());
        jobLog.setJobGroup(currentJob.getJobGroup());
        jobLog.setInvokeTarget(currentJob.getInvokeTarget());
        jobLog.setJobMessage("Manual execution recorded");
        jobLog.setStatus(UserConstants.NORMAL);
        jobLog.setCreateTime(new Date());
        return jobLogMapper.insert(jobLog);
    }

    private void validateCronExpression(String cronExpression) {
        if (cronExpression == null || cronExpression.isBlank()) {
            throw new CustomException(500, "Cron expression can not be empty");
        }
        try {
            CronExpression.parse(cronExpression);
        } catch (IllegalArgumentException ex) {
            throw new CustomException(500, "Invalid cron expression");
        }
    }

    private void enrichNextValidTime(List<SysJob> jobs) {
        if (jobs == null) {
            return;
        }
        jobs.forEach(this::enrichNextValidTime);
    }

    private void enrichNextValidTime(SysJob job) {
        if (job == null || job.getCronExpression() == null || job.getCronExpression().isBlank()) {
            return;
        }
        try {
            LocalDateTime next = CronExpression.parse(job.getCronExpression()).next(LocalDateTime.now());
            if (next != null) {
                job.setNextValidTime(Date.from(next.atZone(ZoneId.systemDefault()).toInstant()));
            }
        } catch (IllegalArgumentException ignored) {
            job.setNextValidTime(null);
        }
    }
}
