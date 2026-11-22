package com.fqq.futurecommunity.web.controller.monitor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.system.domain.SysOperLog;
import com.fqq.futurecommunity.system.service.SysOperLogService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/monitor/operlog")
public class SysOperLogController extends BaseController {
    @Resource
    private SysOperLogService operLogService;

    @GetMapping("/list")
    @PreAuthorize("@pe.hasPerms('monitor:operlog:list')")
    public PageResult<SysOperLog> list(SysOperLog operLog) {
        Page<SysOperLog> page = startPage();
        return getData(operLogService.selectOperLogList(page, operLog));
    }

    @DeleteMapping("/{operIds}")
    @PreAuthorize("@pe.hasPerms('monitor:operlog:remove')")
    public BaseResponse<Void> remove(@PathVariable Long[] operIds) {
        return toAjax(operLogService.deleteOperLogByIds(operIds));
    }

    @DeleteMapping("/clean")
    @PreAuthorize("@pe.hasPerms('monitor:operlog:remove')")
    public BaseResponse<Void> clean() {
        operLogService.cleanOperLog();
        return BaseResponse.success();
    }

    @GetMapping("/export")
    @PreAuthorize("@pe.hasPerms('monitor:operlog:export')")
    public ChainedMap export(SysOperLog operLog) {
        Page<SysOperLog> page = new Page<>(1, 100000);
        List<SysOperLog> operLogList = operLogService.selectOperLogList(page, operLog).getRecords();
        String fileName = FileStorageUtils.generateFileName("oper_log", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(operLogList, SysOperLog.class, filePath, "OperLog");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }
}
