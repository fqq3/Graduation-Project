package com.fqq.futurecommunity.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.PaymentRecord;
import com.fqq.futurecommunity.community.domain.dto.PaymentRecordDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** 缴费记录 Mapper */
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {
    Page<PaymentRecordDto> selectPaymentRecordList(Page<PaymentRecordDto> page, @Param("q") PaymentRecord q);

    BigDecimal selectSuccessfulAmountByBillId(@Param("billId") Long billId);
}
