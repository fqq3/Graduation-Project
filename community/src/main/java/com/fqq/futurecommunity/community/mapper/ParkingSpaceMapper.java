package com.fqq.futurecommunity.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.ParkingSpace;
import com.fqq.futurecommunity.community.domain.dto.ParkingSpaceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/** 停车位 Mapper */
@Mapper
public interface ParkingSpaceMapper extends BaseMapper<ParkingSpace> {
    Page<ParkingSpaceDto> selectParkingSpaceList(Page<ParkingSpaceDto> page, @Param("q") ParkingSpace q);
}
