package com.fqq.futurecommunity.web.controller.monitor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.system.domain.SysLogininfor;
import com.fqq.futurecommunity.system.service.SysLogininforService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/monitor/logininfor")
public class SysLogininforController extends BaseController {
    @Resource
    private SysLogininforService logininforService;

    @GetMapping("/list")
    @PreAuthorize("@pe.hasPerms('monitor:logininfor:list')")
    public PageResult<SysLogininfor> list(SysLogininfor logininfor) {
        Page<SysLogininfor> page = startPage();
        return getData(logininforService.selectLogininforList(page, logininfor));
    }

    @DeleteMapping("/{infoIds}")
    @PreAuthorize("@pe.hasPerms('monitor:logininfor:remove')")
    public BaseResponse<Void> remove(@PathVariable Long[] infoIds) {
        return toAjax(logininforService.deleteLogininforByIds(infoIds));
    }

    @DeleteMapping("/clean")
    @PreAuthorize("@pe.hasPerms('monitor:logininfor:remove')")
    public BaseResponse<Void> clean() {
        logininforService.cleanLogininfor();
        return BaseResponse.success();
    }

    @GetMapping("/export")
    @PreAuthorize("@pe.hasPerms('monitor:logininfor:export')")
    public ChainedMap export(SysLogininfor logininfor) {
        Page<SysLogininfor> page = new Page<>(1, 100000);
        List<SysLogininfor> logininforList = logininforService.selectLogininforList(page, logininfor).getRecords();
        String fileName = FileStorageUtils.generateFileName("logininfor", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(logininforList, SysLogininfor.class, filePath, "Logininfor");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }
}
