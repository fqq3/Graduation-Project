package com.fqq.futurecommunity.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.constant.HttpStatus;
import com.fqq.futurecommunity.common.core.exception.CustomException;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.community.domain.FeeBill;
import com.fqq.futurecommunity.community.domain.dto.FeeBillDto;
import com.fqq.futurecommunity.community.mapper.FeeBillMapper;
import com.fqq.futurecommunity.community.service.FeeBillService;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/** 费用账单 ServiceImpl */
@Service
public class FeeBillServiceImpl implements FeeBillService {
    @Resource
    private FeeBillMapper feeBillMapper;

    @Override
    public Page<FeeBillDto> selectFeeBillList(Page<FeeBillDto> page, FeeBill q) {
        return feeBillMapper.selectFeeBillList(page, q);
    }

    @Override
    public FeeBill selectFeeBillById(Long id) {
        return feeBillMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFeeBill(FeeBill q) {
        if (q.getBillNo() == null || q.getBillNo().isBlank()) {
            q.setBillNo("ZD_" + System.currentTimeMillis());
        }
        if (q.getReceivedAmount() == null) {
            q.setReceivedAmount(BigDecimal.ZERO);
        }
        if (q.getReceivableAmount() != null && q.getUnpaidAmount() == null) {
            q.setUnpaidAmount(q.getReceivableAmount().subtract(q.getReceivedAmount()));
        }
        refreshBillStatus(q);
        fillCreate(q);
        int rows = feeBillMapper.insert(q);

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFeeBill(FeeBill q) {
        if (q.getBillId() == null) {
            throw new CustomException(HttpStatus.ERROR, "费用账单ID不能为空");
        }
        FeeBill old = feeBillMapper.selectById(q.getBillId());
        if (old == null) {
            throw new CustomException(HttpStatus.ERROR, "费用账单不存在");
        }
        mergeAmountFields(old, q);
        refreshBillStatus(q);
        fillUpdate(q);
        return feeBillMapper.updateById(q);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFeeBillByIds(Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            rows += feeBillMapper.deleteById(id);
        }
        return rows;
    }

    private void fillCreate(FeeBill q) {
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

    private void fillUpdate(FeeBill q) {
        q.setUpdateTime(new Date());
        try {
            q.setUpdateBy(SecurityUtils.getUserName());
        } catch (Exception ignored) {
            q.setUpdateBy("admin");
        }
    }


    private void mergeAmountFields(FeeBill old, FeeBill q) {
        if (q.getReceivableAmount() == null) {
            q.setReceivableAmount(old.getReceivableAmount());
        }
        if (q.getReceivedAmount() == null) {
            q.setReceivedAmount(old.getReceivedAmount());
        }
    }

    private void refreshBillStatus(FeeBill q) {
        if (q.getReceivableAmount() == null) {
            return;
        }
        if (q.getReceivedAmount() == null) {
            q.setReceivedAmount(BigDecimal.ZERO);
        }
        if (q.getReceivedAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException(HttpStatus.ERROR, "已收金额不能小于0");
        }
        BigDecimal unpaidAmount = q.getReceivableAmount().subtract(q.getReceivedAmount());
        if (unpaidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            q.setUnpaidAmount(BigDecimal.ZERO);
            q.setBillStatus("2");
        } else {
            q.setUnpaidAmount(unpaidAmount);
            if (q.getReceivedAmount().compareTo(BigDecimal.ZERO) > 0) {
                q.setBillStatus("1");
            } else {
                q.setBillStatus("0");
            }
        }
    }

}
