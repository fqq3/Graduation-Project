package com.fqq.futurecommunity.web.controller.monitor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.system.domain.SysJobLog;
import com.fqq.futurecommunity.system.service.SysJobLogService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/monitor/jobLog")
public class SysJobLogController extends BaseController {

    @Resource
    private SysJobLogService jobLogService;

    @GetMapping("/list")
    @PreAuthorize("@pe.hasAnyPerms('monitor:job:list,monitor:job:query')")
    public PageResult<SysJobLog> list(SysJobLog jobLog) {
        Page<SysJobLog> page = startPage();
        return getData(jobLogService.selectJobLogPage(page, jobLog));
    }

    @DeleteMapping("/{jobLogIds}")
    @PreAuthorize("@pe.hasPerms('monitor:job:remove')")
    public BaseResponse<Void> remove(@PathVariable Long[] jobLogIds) {
        return toAjax(jobLogService.deleteJobLogByIds(jobLogIds));
    }

    @DeleteMapping("/clean")
    @PreAuthorize("@pe.hasPerms('monitor:job:remove')")
    public BaseResponse<Void> clean() {
        jobLogService.cleanJobLog();
        return BaseResponse.success();
    }

    @GetMapping("/export")
    @PreAuthorize("@pe.hasPerms('monitor:job:export')")
    public ChainedMap export(SysJobLog jobLog) {
        List<SysJobLog> jobLogList = jobLogService.selectJobLogList(jobLog);
        String fileName = FileStorageUtils.generateFileName("job_log", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(jobLogList, SysJobLog.class, filePath, "JobLog");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }
}
