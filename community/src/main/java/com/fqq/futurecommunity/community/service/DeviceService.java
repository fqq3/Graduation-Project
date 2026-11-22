package com.fqq.futurecommunity.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.Device;
import com.fqq.futurecommunity.community.domain.dto.DeviceDto;

/** 设备信息 Service */
public interface DeviceService {
    Page<DeviceDto> selectDeviceList(Page<DeviceDto> page, Device q);
    Device selectDeviceById(Long id);
    int insertDevice(Device q);
    int updateDevice(Device q);
    int deleteDeviceByIds(Long[] ids);
}
