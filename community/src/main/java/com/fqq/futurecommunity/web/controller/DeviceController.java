package com.fqq.futurecommunity.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.community.domain.Device;
import com.fqq.futurecommunity.community.domain.dto.DeviceDto;
import com.fqq.futurecommunity.community.service.DeviceService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;

/** 设备信息 Controller */
@RestController
@RequestMapping("/community/device")
public class DeviceController extends BaseController {
    @Resource
    private DeviceService deviceService;

    @GetMapping("/list")
    @PreAuthorize("@pe.hasPerms('community:device:list')")
    public PageResult list(Device q) {
        Page<DeviceDto> page = startPage();
        return getData(deviceService.selectDeviceList(page, q));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@pe.hasPerms('community:device:query')")
    public BaseResponse getInfo(@PathVariable Long id) {
        return BaseResponse.success(deviceService.selectDeviceById(id));
    }

    @PostMapping
    @PreAuthorize("@pe.hasPerms('community:device:add')")
    public BaseResponse add(@RequestBody Device q) {
        return toAjax(deviceService.insertDevice(q));
    }

    @PutMapping
    @PreAuthorize("@pe.hasPerms('community:device:edit')")
    public BaseResponse edit(@RequestBody Device q) {
        return toAjax(deviceService.updateDevice(q));
    }

    @DeleteMapping("/{ids}")
    @PreAuthorize("@pe.hasPerms('community:device:remove')")
    public BaseResponse remove(@PathVariable Long[] ids) {
        return toAjax(deviceService.deleteDeviceByIds(ids));
    }

    @GetMapping("/export")
    @PreAuthorize("@pe.hasPerms('community:device:export')")
    public ChainedMap export(Device q) {
        Page<DeviceDto> page = new Page<>(1, 100000);
        List<DeviceDto> rows = deviceService.selectDeviceList(page, q).getRecords();
        String fileName = FileStorageUtils.generateFileName("device", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(rows, DeviceDto.class, filePath, "Device");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }
}
