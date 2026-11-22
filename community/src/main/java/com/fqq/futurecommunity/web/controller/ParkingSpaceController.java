package com.fqq.futurecommunity.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.community.domain.ParkingSpace;
import com.fqq.futurecommunity.community.domain.dto.ParkingSpaceDto;
import com.fqq.futurecommunity.community.service.ParkingSpaceService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;

/** 停车位 Controller */
@RestController
@RequestMapping("/community/parkingSpace")
public class ParkingSpaceController extends BaseController {
    @Resource
    private ParkingSpaceService parkingSpaceService;

    @GetMapping("/list")
    @PreAuthorize("@pe.hasPerms('community:parkingSpace:list')")
    public PageResult list(ParkingSpace q) {
        Page<ParkingSpaceDto> page = startPage();
        return getData(parkingSpaceService.selectParkingSpaceList(page, q));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@pe.hasPerms('community:parkingSpace:query')")
    public BaseResponse getInfo(@PathVariable Long id) {
        return BaseResponse.success(parkingSpaceService.selectParkingSpaceById(id));
    }

    @PostMapping
    @PreAuthorize("@pe.hasPerms('community:parkingSpace:add')")
    public BaseResponse add(@RequestBody ParkingSpace q) {
        return toAjax(parkingSpaceService.insertParkingSpace(q));
    }

    @PutMapping
    @PreAuthorize("@pe.hasPerms('community:parkingSpace:edit')")
    public BaseResponse edit(@RequestBody ParkingSpace q) {
        return toAjax(parkingSpaceService.updateParkingSpace(q));
    }

    @DeleteMapping("/{ids}")
    @PreAuthorize("@pe.hasPerms('community:parkingSpace:remove')")
    public BaseResponse remove(@PathVariable Long[] ids) {
        return toAjax(parkingSpaceService.deleteParkingSpaceByIds(ids));
    }

    @GetMapping("/export")
    @PreAuthorize("@pe.hasPerms('community:parkingSpace:export')")
    public ChainedMap export(ParkingSpace q) {
        Page<ParkingSpaceDto> page = new Page<>(1, 100000);
        List<ParkingSpaceDto> rows = parkingSpaceService.selectParkingSpaceList(page, q).getRecords();
        String fileName = FileStorageUtils.generateFileName("parking_space", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(rows, ParkingSpaceDto.class, filePath, "ParkingSpace");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }
}
