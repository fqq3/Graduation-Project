package com.fqq.futurecommunity.community.service.impl;

import com.fqq.futurecommunity.community.domain.FeeBill;
import com.fqq.futurecommunity.community.mapper.FeeBillMapper;
import com.fqq.futurecommunity.community.service.FinanceStatService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 财务统计服务实现 */
@Service
public class FinanceStatServiceImpl implements FinanceStatService {

    @Resource
    private FeeBillMapper feeBillMapper;

    @Override
    public Map<String, Object> getOverview(FeeBill q) {
        Map<String, Object> overview = feeBillMapper.selectFinanceOverview(q);
        if (overview == null) {
            overview = new HashMap<>();
        }
        overview.put("paymentRate", calculatePaymentRate(overview.get("paidBillCount"), overview.get("billCount")));
        return overview;
    }

    @Override
    public List<Map<String, Object>> getMonthStat(FeeBill q) {
        return feeBillMapper.selectFinanceMonthStat(q);
    }

    @Override
    public List<Map<String, Object>> getItemStat(FeeBill q) {
        return feeBillMapper.selectFinanceItemStat(q);
    }

    @Override
    public Map<String, Object> getAll(FeeBill q) {
        Map<String, Object> data = new HashMap<>();
        data.put("overview", getOverview(q));
        data.put("month", getMonthStat(q));
        data.put("item", getItemStat(q));
        return data;
    }

    private BigDecimal calculatePaymentRate(Object paidValue, Object billValue) {
        BigDecimal paid = toBigDecimal(paidValue);
        BigDecimal bill = toBigDecimal(billValue);
        if (bill.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return paid.divide(bill, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        try {
            return new BigDecimal(String.valueOf(value));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
