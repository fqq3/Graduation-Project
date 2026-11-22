package com.fqq.futurecommunity.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.community.domain.Vehicle;
import com.fqq.futurecommunity.community.domain.dto.VehicleDto;
import com.fqq.futurecommunity.community.service.VehicleService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;

/** 车辆信息 Controller */
@RestController
@RequestMapping("/community/vehicle")
public class VehicleController extends BaseController {
    @Resource
    private VehicleService vehicleService;

    @GetMapping("/list")
    @PreAuthorize("@pe.hasPerms('community:vehicle:list')")
    public PageResult list(Vehicle q) {
        Page<VehicleDto> page = startPage();
        return getData(vehicleService.selectVehicleList(page, q));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@pe.hasPerms('community:vehicle:query')")
    public BaseResponse getInfo(@PathVariable Long id) {
        return BaseResponse.success(vehicleService.selectVehicleById(id));
    }

    @PostMapping
    @PreAuthorize("@pe.hasPerms('community:vehicle:add')")
    public BaseResponse add(@RequestBody Vehicle q) {
        return toAjax(vehicleService.insertVehicle(q));
    }

    @PutMapping
    @PreAuthorize("@pe.hasPerms('community:vehicle:edit')")
    public BaseResponse edit(@RequestBody Vehicle q) {
        return toAjax(vehicleService.updateVehicle(q));
    }

    @DeleteMapping("/{ids}")
    @PreAuthorize("@pe.hasPerms('community:vehicle:remove')")
    public BaseResponse remove(@PathVariable Long[] ids) {
        return toAjax(vehicleService.deleteVehicleByIds(ids));
    }

    @GetMapping("/export")
    @PreAuthorize("@pe.hasPerms('community:vehicle:export')")
    public ChainedMap export(Vehicle q) {
        Page<VehicleDto> page = new Page<>(1, 100000);
        List<VehicleDto> rows = vehicleService.selectVehicleList(page, q).getRecords();
        String fileName = FileStorageUtils.generateFileName("vehicle", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(rows, VehicleDto.class, filePath, "Vehicle");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }
}
