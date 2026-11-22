package com.fqq.futurecommunity.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.constant.HttpStatus;
import com.fqq.futurecommunity.common.core.exception.CustomException;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.community.domain.FeeBill;
import com.fqq.futurecommunity.community.domain.PaymentRecord;
import com.fqq.futurecommunity.community.domain.dto.PaymentRecordDto;
import com.fqq.futurecommunity.community.mapper.FeeBillMapper;
import com.fqq.futurecommunity.community.mapper.PaymentRecordMapper;
import com.fqq.futurecommunity.community.service.PaymentRecordService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/** 缴费记录 ServiceImpl */
@Service
public class PaymentRecordServiceImpl implements PaymentRecordService {
    @Resource
    private PaymentRecordMapper paymentRecordMapper;
    @Resource
    private FeeBillMapper feeBillMapper;

    @Override
    public Page<PaymentRecordDto> selectPaymentRecordList(Page<PaymentRecordDto> page, PaymentRecord q) {
        return paymentRecordMapper.selectPaymentRecordList(page, q);
    }

    @Override
    public PaymentRecord selectPaymentRecordById(Long id) {
        return paymentRecordMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPaymentRecord(PaymentRecord q) {
        if (q.getPaymentNo() == null || q.getPaymentNo().isBlank()) {
            q.setPaymentNo("JF_" + System.currentTimeMillis());
        }
        if (q.getPaymentStatus() == null || q.getPaymentStatus().isBlank()) {
            q.setPaymentStatus("1");
        }
        if (q.getPaymentTime() == null) {
            q.setPaymentTime(LocalDateTime.now());
        }
        fillCreate(q);
        int rows = paymentRecordMapper.insert(q);
        syncBillAmount(q.getBillId());
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePaymentRecord(PaymentRecord q) {
        PaymentRecord before = paymentRecordMapper.selectById(q.getPaymentId());
        fillUpdate(q);
        int rows = paymentRecordMapper.updateById(q);
        if (before != null) {
            syncBillAmount(before.getBillId());
        }
        syncBillAmount(q.getBillId());
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePaymentRecordByIds(Long[] ids) {
        int rows = 0;
        Set<Long> billIds = new HashSet<>();
        for (Long id : ids) {
            PaymentRecord paymentRecord = paymentRecordMapper.selectById(id);
            if (paymentRecord != null && paymentRecord.getBillId() != null) {
                billIds.add(paymentRecord.getBillId());
            }
            rows += paymentRecordMapper.deleteById(id);
        }
        for (Long billId : billIds) {
            syncBillAmount(billId);
        }
        return rows;
    }

    private void fillCreate(PaymentRecord q) {
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

    private void fillUpdate(PaymentRecord q) {
        q.setUpdateTime(new Date());
        try {
            q.setUpdateBy(SecurityUtils.getUserName());
        } catch (Exception ignored) {
            q.setUpdateBy("admin");
        }
    }

    private void syncBillAmount(Long billId) {
        if (billId == null) {
            return;
        }
        FeeBill bill = feeBillMapper.selectById(billId);
        if (bill == null) {
            throw new CustomException(HttpStatus.ERROR, "对应账单不存在");
        }
        BigDecimal receivable = bill.getReceivableAmount() == null ? BigDecimal.ZERO : bill.getReceivableAmount();
        BigDecimal received = paymentRecordMapper.selectSuccessfulAmountByBillId(billId);
        if (received == null) {
            received = BigDecimal.ZERO;
        }
        BigDecimal unpaid = receivable.subtract(received);
        if (unpaid.compareTo(BigDecimal.ZERO) <= 0) {
            unpaid = BigDecimal.ZERO;
            bill.setBillStatus("2");
        } else if (received.compareTo(BigDecimal.ZERO) > 0) {
            bill.setBillStatus("1");
        } else {
            bill.setBillStatus("0");
        }
        bill.setReceivedAmount(received);
        bill.setUnpaidAmount(unpaid);
        bill.setUpdateTime(new Date());
        try {
            bill.setUpdateBy(SecurityUtils.getUserName());
        } catch (Exception ignored) {
            bill.setUpdateBy("admin");
        }
        feeBillMapper.updateById(bill);
    }
}
