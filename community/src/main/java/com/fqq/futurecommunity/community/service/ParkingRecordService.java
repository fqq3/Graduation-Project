package com.fqq.futurecommunity.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.ParkingRecord;
import com.fqq.futurecommunity.community.domain.dto.ParkingRecordDto;

/** 停车记录 Service */
public interface ParkingRecordService {
    Page<ParkingRecordDto> selectParkingRecordList(Page<ParkingRecordDto> page, ParkingRecord q);
    ParkingRecord selectParkingRecordById(Long id);
    int insertParkingRecord(ParkingRecord q);
    int updateParkingRecord(ParkingRecord q);
    int deleteParkingRecordByIds(Long[] ids);
}
