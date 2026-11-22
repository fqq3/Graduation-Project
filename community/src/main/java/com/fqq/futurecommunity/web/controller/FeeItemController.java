package com.fqq.futurecommunity.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.community.domain.FeeItem;
import com.fqq.futurecommunity.community.domain.dto.FeeItemDto;
import com.fqq.futurecommunity.community.service.FeeItemService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;

/** 费用项目 Controller */
@RestController
@RequestMapping("/community/feeItem")
public class FeeItemController extends BaseController {
    @Resource
    private FeeItemService feeItemService;

    @GetMapping("/list")
    @PreAuthorize("@pe.hasPerms('community:feeItem:list')")
    public PageResult list(FeeItem q) {
        Page<FeeItemDto> page = startPage();
        return getData(feeItemService.selectFeeItemList(page, q));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@pe.hasPerms('community:feeItem:query')")
    public BaseResponse getInfo(@PathVariable Long id) {
        return BaseResponse.success(feeItemService.selectFeeItemById(id));
    }

    @PostMapping
    @PreAuthorize("@pe.hasPerms('community:feeItem:add')")
    public BaseResponse add(@RequestBody FeeItem q) {
        return toAjax(feeItemService.insertFeeItem(q));
    }

    @PutMapping
    @PreAuthorize("@pe.hasPerms('community:feeItem:edit')")
    public BaseResponse edit(@RequestBody FeeItem q) {
        return toAjax(feeItemService.updateFeeItem(q));
    }

    @DeleteMapping("/{ids}")
    @PreAuthorize("@pe.hasPerms('community:feeItem:remove')")
    public BaseResponse remove(@PathVariable Long[] ids) {
        return toAjax(feeItemService.deleteFeeItemByIds(ids));
    }

    @GetMapping("/export")
    @PreAuthorize("@pe.hasPerms('community:feeItem:export')")
    public ChainedMap export(FeeItem q) {
        Page<FeeItemDto> page = new Page<>(1, 100000);
        List<FeeItemDto> rows = feeItemService.selectFeeItemList(page, q).getRecords();
        String fileName = FileStorageUtils.generateFileName("fee_item", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(rows, FeeItemDto.class, filePath, "FeeItem");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }
}
