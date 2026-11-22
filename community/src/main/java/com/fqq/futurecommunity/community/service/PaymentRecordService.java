package com.fqq.futurecommunity.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.PaymentRecord;
import com.fqq.futurecommunity.community.domain.dto.PaymentRecordDto;

/** 缴费记录 Service */
public interface PaymentRecordService {
    Page<PaymentRecordDto> selectPaymentRecordList(Page<PaymentRecordDto> page, PaymentRecord q);
    PaymentRecord selectPaymentRecordById(Long id);
    int insertPaymentRecord(PaymentRecord q);
    int updatePaymentRecord(PaymentRecord q);
    int deletePaymentRecordByIds(Long[] ids);
}
