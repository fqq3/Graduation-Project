package com.fqq.futurecommunity.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.constant.HttpStatus;
import com.fqq.futurecommunity.common.core.exception.CustomException;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.community.domain.RepairOrder;
import com.fqq.futurecommunity.community.domain.dto.RepairOrderDto;
import com.fqq.futurecommunity.community.mapper.RepairOrderMapper;
import com.fqq.futurecommunity.community.service.RepairOrderService;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/** 报修工单 ServiceImpl */
@Service
public class RepairOrderServiceImpl implements RepairOrderService {
    @Resource
    private RepairOrderMapper repairOrderMapper;

    @Override
    public Page<RepairOrderDto> selectRepairOrderList(Page<RepairOrderDto> page, RepairOrder q) {
        return repairOrderMapper.selectRepairOrderList(page, q);
    }

    @Override
    public RepairOrder selectRepairOrderById(Long id) {
        return repairOrderMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRepairOrder(RepairOrder q) {
        if (q.getRepairNo() == null || q.getRepairNo().isBlank()) {
            q.setRepairNo("BX_" + System.currentTimeMillis());
        }
        if (q.getAuditStatus() == null || q.getAuditStatus().isBlank()) {
            q.setAuditStatus("0");
        }
        if (q.getStatus() == null || q.getStatus().isBlank()) {
            q.setStatus("0");
        }
        repairStatusSync(q);
        fillCreate(q);
        int rows = repairOrderMapper.insert(q);

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRepairOrder(RepairOrder q) {
        repairStatusSync(q);
        fillUpdate(q);
        return repairOrderMapper.updateById(q);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRepairOrderByIds(Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            rows += repairOrderMapper.deleteById(id);
        }
        return rows;
    }

    private void fillCreate(RepairOrder q) {
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

    private void fillUpdate(RepairOrder q) {
        q.setUpdateTime(new Date());
        try {
            q.setUpdateBy(SecurityUtils.getUserName());
        } catch (Exception ignored) {
            q.setUpdateBy("admin");
        }
    }


    private void repairStatusSync(RepairOrder q) {
        if ("2".equals(q.getAuditStatus())) {
            q.setStatus("4");
        } else if ("1".equals(q.getAuditStatus()) && (q.getStatus() == null || "0".equals(q.getStatus()))) {
            q.setStatus("1");
        }
        if (q.getRepairStartTime() != null && (q.getStatus() == null || "1".equals(q.getStatus()))) {
            q.setStatus("2");
        }
        if (q.getRepairFinishTime() != null) {
            q.setStatus("3");
        }
    }

}
