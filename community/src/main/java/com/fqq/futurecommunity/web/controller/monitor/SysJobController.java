package com.fqq.futurecommunity.web.controller.monitor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.system.domain.SysJob;
import com.fqq.futurecommunity.system.service.SysJobService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/monitor/job")
public class SysJobController extends BaseController {

    @Resource
    private SysJobService jobService;

    @GetMapping("/list")
    @PreAuthorize("@pe.hasAnyPerms('monitor:job:list,monitor:job:query')")
    public PageResult<SysJob> list(SysJob job) {
        Page<SysJob> page = startPage();
        return getData(jobService.selectJobPage(page, job));
    }

    @GetMapping("/{jobId}")
    @PreAuthorize("@pe.hasAnyPerms('monitor:job:list,monitor:job:query')")
    public BaseResponse<SysJob> getInfo(@PathVariable Long jobId) {
        return BaseResponse.success(jobService.selectJobById(jobId));
    }

    @PostMapping
    @PreAuthorize("@pe.hasPerms('monitor:job:add')")
    public BaseResponse<Void> add(@RequestBody SysJob job) {
        job.setCreateBy(SecurityUtils.getUserName());
        return toAjax(jobService.insertJob(job));
    }

    @PutMapping
    @PreAuthorize("@pe.hasPerms('monitor:job:edit')")
    public BaseResponse<Void> edit(@RequestBody SysJob job) {
        job.setUpdateBy(SecurityUtils.getUserName());
        return toAjax(jobService.updateJob(job));
    }

    @DeleteMapping("/{jobIds}")
    @PreAuthorize("@pe.hasPerms('monitor:job:remove')")
    public BaseResponse<Void> remove(@PathVariable Long[] jobIds) {
        return toAjax(jobService.deleteJobByIds(jobIds));
    }

    @PutMapping("/changeStatus")
    @PreAuthorize("@pe.hasPerms('monitor:job:changeStatus')")
    public BaseResponse<Void> changeStatus(@RequestBody SysJob job) {
        return toAjax(jobService.changeStatus(job));
    }

    @PutMapping("/run")
    @PreAuthorize("@pe.hasPerms('monitor:job:changeStatus')")
    public BaseResponse<Void> run(@RequestBody SysJob job) {
        return toAjax(jobService.run(job));
    }

    @GetMapping("/export")
    @PreAuthorize("@pe.hasPerms('monitor:job:export')")
    public ChainedMap export(SysJob job) {
        List<SysJob> jobList = jobService.selectJobList(job);
        String fileName = FileStorageUtils.generateFileName("job", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(jobList, SysJob.class, filePath, "Job");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }
}
