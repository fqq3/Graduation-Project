package com.fqq.futurecommunity.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.Device;
import com.fqq.futurecommunity.community.domain.dto.DeviceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/** 设备信息 Mapper */
@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
    Page<DeviceDto> selectDeviceList(Page<DeviceDto> page, @Param("q") Device q);
}
