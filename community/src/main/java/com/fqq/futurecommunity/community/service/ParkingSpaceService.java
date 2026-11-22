package com.fqq.futurecommunity.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.ParkingSpace;
import com.fqq.futurecommunity.community.domain.dto.ParkingSpaceDto;

/** 停车位 Service */
public interface ParkingSpaceService {
    Page<ParkingSpaceDto> selectParkingSpaceList(Page<ParkingSpaceDto> page, ParkingSpace q);
    ParkingSpace selectParkingSpaceById(Long id);
    int insertParkingSpace(ParkingSpace q);
    int updateParkingSpace(ParkingSpace q);
    int deleteParkingSpaceByIds(Long[] ids);
}
