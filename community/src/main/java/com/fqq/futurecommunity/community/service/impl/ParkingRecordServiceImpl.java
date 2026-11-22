package com.fqq.futurecommunity.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.constant.HttpStatus;
import com.fqq.futurecommunity.common.core.exception.CustomException;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.community.domain.ParkingRecord;
import com.fqq.futurecommunity.community.domain.dto.ParkingRecordDto;
import com.fqq.futurecommunity.community.mapper.ParkingRecordMapper;
import com.fqq.futurecommunity.community.service.ParkingRecordService;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;

/** 停车记录 ServiceImpl */
@Service
public class ParkingRecordServiceImpl implements ParkingRecordService {
    @Resource
    private ParkingRecordMapper parkingRecordMapper;

    @Override
    public Page<ParkingRecordDto> selectParkingRecordList(Page<ParkingRecordDto> page, ParkingRecord q) {
        return parkingRecordMapper.selectParkingRecordList(page, q);
    }

    @Override
    public ParkingRecord selectParkingRecordById(Long id) {
        return parkingRecordMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertParkingRecord(ParkingRecord q) {
        refreshParkingDuration(q.getInTime(), q.getOutTime(), q);
        if (q.getPayStatus() == null || q.getPayStatus().isBlank()) {
            q.setPayStatus("0");
        }
        fillCreate(q);
        int rows = parkingRecordMapper.insert(q);

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateParkingRecord(ParkingRecord q) {
        if (q.getRecordId() == null) {
            throw new CustomException(HttpStatus.ERROR, "停车记录ID不能为空");
        }
        ParkingRecord old = parkingRecordMapper.selectById(q.getRecordId());
        if (old == null) {
            throw new CustomException(HttpStatus.ERROR, "停车记录不存在");
        }
        refreshParkingDuration(
                q.getInTime() != null ? q.getInTime() : old.getInTime(),
                q.getOutTime() != null ? q.getOutTime() : old.getOutTime(),
                q
        );
        fillUpdate(q);
        return parkingRecordMapper.updateById(q);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteParkingRecordByIds(Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            rows += parkingRecordMapper.deleteById(id);
        }
        return rows;
    }

    private void refreshParkingDuration(java.time.LocalDateTime inTime, java.time.LocalDateTime outTime, ParkingRecord target) {
        if (inTime == null || outTime == null) {
            return;
        }
        long minutes = Duration.between(inTime, outTime).toMinutes();
        if (minutes < 0) {
            throw new CustomException(HttpStatus.ERROR, "出场时间不能早于入场时间");
        }
        target.setParkingDuration((int) minutes);
    }

    private void fillCreate(ParkingRecord q) {
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

    private void fillUpdate(ParkingRecord q) {
        q.setUpdateTime(new Date());
        try {
            q.setUpdateBy(SecurityUtils.getUserName());
        } catch (Exception ignored) {
            q.setUpdateBy("admin");
        }
    }


}
