package com.fqq.futurecommunity.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.community.domain.ParkingRecord;
import com.fqq.futurecommunity.community.domain.dto.ParkingRecordDto;
import com.fqq.futurecommunity.community.service.ParkingRecordService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;

/** 停车记录 Controller */
@RestController
@RequestMapping("/community/parkingRecord")
public class ParkingRecordController extends BaseController {
    @Resource
    private ParkingRecordService parkingRecordService;

    @GetMapping("/list")
    @PreAuthorize("@pe.hasPerms('community:parkingRecord:list')")
    public PageResult list(ParkingRecord q) {
        Page<ParkingRecordDto> page = startPage();
        return getData(parkingRecordService.selectParkingRecordList(page, q));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@pe.hasPerms('community:parkingRecord:query')")
    public BaseResponse getInfo(@PathVariable Long id) {
        return BaseResponse.success(parkingRecordService.selectParkingRecordById(id));
    }

    @PostMapping
    @PreAuthorize("@pe.hasPerms('community:parkingRecord:add')")
    public BaseResponse add(@RequestBody ParkingRecord q) {
        return toAjax(parkingRecordService.insertParkingRecord(q));
    }

    @PutMapping
    @PreAuthorize("@pe.hasPerms('community:parkingRecord:edit')")
    public BaseResponse edit(@RequestBody ParkingRecord q) {
        return toAjax(parkingRecordService.updateParkingRecord(q));
    }

    @DeleteMapping("/{ids}")
    @PreAuthorize("@pe.hasPerms('community:parkingRecord:remove')")
    public BaseResponse remove(@PathVariable Long[] ids) {
        return toAjax(parkingRecordService.deleteParkingRecordByIds(ids));
    }

    @GetMapping("/export")
    @PreAuthorize("@pe.hasPerms('community:parkingRecord:export')")
    public ChainedMap export(ParkingRecord q) {
        Page<ParkingRecordDto> page = new Page<>(1, 100000);
        List<ParkingRecordDto> rows = parkingRecordService.selectParkingRecordList(page, q).getRecords();
        String fileName = FileStorageUtils.generateFileName("parking_record", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(rows, ParkingRecordDto.class, filePath, "ParkingRecord");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }
}
