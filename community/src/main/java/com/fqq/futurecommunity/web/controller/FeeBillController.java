package com.fqq.futurecommunity.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.community.domain.FeeBill;
import com.fqq.futurecommunity.community.domain.dto.FeeBillDto;
import com.fqq.futurecommunity.community.service.FeeBillService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;

/** 费用账单 Controller */
@RestController
@RequestMapping("/community/feeBill")
public class FeeBillController extends BaseController {
    @Resource
    private FeeBillService feeBillService;

    @GetMapping("/list")
    @PreAuthorize("@pe.hasPerms('community:feeBill:list')")
    public PageResult list(FeeBill q) {
        Page<FeeBillDto> page = startPage();
        return getData(feeBillService.selectFeeBillList(page, q));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@pe.hasPerms('community:feeBill:query')")
    public BaseResponse getInfo(@PathVariable Long id) {
        return BaseResponse.success(feeBillService.selectFeeBillById(id));
    }

    @PostMapping
    @PreAuthorize("@pe.hasPerms('community:feeBill:add')")
    public BaseResponse add(@RequestBody FeeBill q) {
        return toAjax(feeBillService.insertFeeBill(q));
    }

    @PutMapping
    @PreAuthorize("@pe.hasPerms('community:feeBill:edit')")
    public BaseResponse edit(@RequestBody FeeBill q) {
        return toAjax(feeBillService.updateFeeBill(q));
    }

    @DeleteMapping("/{ids}")
    @PreAuthorize("@pe.hasPerms('community:feeBill:remove')")
    public BaseResponse remove(@PathVariable Long[] ids) {
        return toAjax(feeBillService.deleteFeeBillByIds(ids));
    }

    @GetMapping("/export")
    @PreAuthorize("@pe.hasPerms('community:feeBill:export')")
    public ChainedMap export(FeeBill q) {
        Page<FeeBillDto> page = new Page<>(1, 100000);
        List<FeeBillDto> rows = feeBillService.selectFeeBillList(page, q).getRecords();
        String fileName = FileStorageUtils.generateFileName("fee_bill", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(rows, FeeBillDto.class, filePath, "FeeBill");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }
}
