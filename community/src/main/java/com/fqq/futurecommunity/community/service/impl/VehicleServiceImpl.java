package com.fqq.futurecommunity.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.constant.HttpStatus;
import com.fqq.futurecommunity.common.core.exception.CustomException;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.community.domain.Vehicle;
import com.fqq.futurecommunity.community.domain.dto.VehicleDto;
import com.fqq.futurecommunity.community.mapper.VehicleMapper;
import com.fqq.futurecommunity.community.service.VehicleService;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/** 车辆信息 ServiceImpl */
@Service
public class VehicleServiceImpl implements VehicleService {
    @Resource
    private VehicleMapper vehicleMapper;

    @Override
    public Page<VehicleDto> selectVehicleList(Page<VehicleDto> page, Vehicle q) {
        return vehicleMapper.selectVehicleList(page, q);
    }

    @Override
    public Vehicle selectVehicleById(Long id) {
        return vehicleMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertVehicle(Vehicle q) {
        if (q.getBindStatus() == null || q.getBindStatus().isBlank()) {
            q.setBindStatus(q.getOwnerId() == null ? "1" : "0");
        }
        if (q.getStatus() == null || q.getStatus().isBlank()) {
            q.setStatus("0");
        }
        fillCreate(q);
        int rows = vehicleMapper.insert(q);

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateVehicle(Vehicle q) {

        fillUpdate(q);
        return vehicleMapper.updateById(q);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteVehicleByIds(Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            rows += vehicleMapper.deleteById(id);
        }
        return rows;
    }

    private void fillCreate(Vehicle q) {
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

    private void fillUpdate(Vehicle q) {
        q.setUpdateTime(new Date());
        try {
            q.setUpdateBy(SecurityUtils.getUserName());
        } catch (Exception ignored) {
            q.setUpdateBy("admin");
        }
    }


}
