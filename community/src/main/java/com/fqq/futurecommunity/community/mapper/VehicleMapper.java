package com.fqq.futurecommunity.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.Vehicle;
import com.fqq.futurecommunity.community.domain.dto.VehicleDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/** 车辆信息 Mapper */
@Mapper
public interface VehicleMapper extends BaseMapper<Vehicle> {
    Page<VehicleDto> selectVehicleList(Page<VehicleDto> page, @Param("q") Vehicle q);
}
