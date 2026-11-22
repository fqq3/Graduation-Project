package com.fqq.futurecommunity.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.Vehicle;
import com.fqq.futurecommunity.community.domain.dto.VehicleDto;

/** 车辆信息 Service */
public interface VehicleService {
    Page<VehicleDto> selectVehicleList(Page<VehicleDto> page, Vehicle q);
    Vehicle selectVehicleById(Long id);
    int insertVehicle(Vehicle q);
    int updateVehicle(Vehicle q);
    int deleteVehicleByIds(Long[] ids);
}
