package com.fqq.futurecommunity.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.constant.HttpStatus;
import com.fqq.futurecommunity.common.core.exception.CustomException;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.community.domain.ParkingSpace;
import com.fqq.futurecommunity.community.domain.dto.ParkingSpaceDto;
import com.fqq.futurecommunity.community.mapper.ParkingSpaceMapper;
import com.fqq.futurecommunity.community.service.ParkingSpaceService;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/** 停车位 ServiceImpl */
@Service
public class ParkingSpaceServiceImpl implements ParkingSpaceService {
    @Resource
    private ParkingSpaceMapper parkingSpaceMapper;

    @Override
    public Page<ParkingSpaceDto> selectParkingSpaceList(Page<ParkingSpaceDto> page, ParkingSpace q) {
        return parkingSpaceMapper.selectParkingSpaceList(page, q);
    }

    @Override
    public ParkingSpace selectParkingSpaceById(Long id) {
        return parkingSpaceMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertParkingSpace(ParkingSpace q) {
        if (q.getUseStatus() == null || q.getUseStatus().isBlank()) {
            q.setUseStatus(q.getVehicleId() == null ? "0" : "1");
        }
        fillCreate(q);
        int rows = parkingSpaceMapper.insert(q);

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateParkingSpace(ParkingSpace q) {

        fillUpdate(q);
        return parkingSpaceMapper.updateById(q);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteParkingSpaceByIds(Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            rows += parkingSpaceMapper.deleteById(id);
        }
        return rows;
    }

    private void fillCreate(ParkingSpace q) {
        Date now = new Date();
        q.setCreateTime(now);
        q.setUpdateTime(now);
        try {
            String username = SecurityUtils.getUserName();
            q.setCreateBy(username);
            q.setUpdateBy(username);
        } catch (Exception ignored) {
            q.setCreateBy("admin");
            q.setUpdateBy("admin");
        }
    }

    private void fillUpdate(ParkingSpace q) {
        q.setUpdateTime(new Date());
        try {
            q.setUpdateBy(SecurityUtils.getUserName());
        } catch (Exception ignored) {
            q.setUpdateBy("admin");
        }
    }


}
