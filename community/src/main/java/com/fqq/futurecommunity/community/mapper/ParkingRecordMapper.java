package com.fqq.futurecommunity.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.ParkingRecord;
import com.fqq.futurecommunity.community.domain.dto.ParkingRecordDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/** 停车记录 Mapper */
@Mapper
public interface ParkingRecordMapper extends BaseMapper<ParkingRecord> {
    Page<ParkingRecordDto> selectParkingRecordList(Page<ParkingRecordDto> page, @Param("q") ParkingRecord q);
}
