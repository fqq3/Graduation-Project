package com.fqq.futurecommunity.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.community.domain.RepairOrder;
import com.fqq.futurecommunity.community.domain.dto.RepairOrderDto;
import com.fqq.futurecommunity.community.service.RepairOrderService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;

/** 报修工单 Controller */
@RestController
@RequestMapping("/community/repairOrder")
public class RepairOrderController extends BaseController {
    @Resource
    private RepairOrderService repairOrderService;

    @GetMapping("/list")
    @PreAuthorize("@pe.hasPerms('community:repairOrder:list')")
    public PageResult list(RepairOrder q) {
        Page<RepairOrderDto> page = startPage();
        return getData(repairOrderService.selectRepairOrderList(page, q));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@pe.hasPerms('community:repairOrder:query')")
    public BaseResponse getInfo(@PathVariable Long id) {
        return BaseResponse.success(repairOrderService.selectRepairOrderById(id));
    }

    @PostMapping
    @PreAuthorize("@pe.hasPerms('community:repairOrder:add')")
    public BaseResponse add(@RequestBody RepairOrder q) {
        return toAjax(repairOrderService.insertRepairOrder(q));
    }

    @PutMapping
    @PreAuthorize("@pe.hasPerms('community:repairOrder:edit')")
    public BaseResponse edit(@RequestBody RepairOrder q) {
        return toAjax(repairOrderService.updateRepairOrder(q));
    }

    @DeleteMapping("/{ids}")
    @PreAuthorize("@pe.hasPerms('community:repairOrder:remove')")
    public BaseResponse remove(@PathVariable Long[] ids) {
        return toAjax(repairOrderService.deleteRepairOrderByIds(ids));
    }

    @GetMapping("/export")
    @PreAuthorize("@pe.hasPerms('community:repairOrder:export')")
    public ChainedMap export(RepairOrder q) {
        Page<RepairOrderDto> page = new Page<>(1, 100000);
        List<RepairOrderDto> rows = repairOrderService.selectRepairOrderList(page, q).getRecords();
        String fileName = FileStorageUtils.generateFileName("repair_order", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(rows, RepairOrderDto.class, filePath, "RepairOrder");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }
}
