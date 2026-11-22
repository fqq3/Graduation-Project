package com.fqq.futurecommunity.community.service;

import com.fqq.futurecommunity.community.domain.FeeBill;

import java.util.List;
import java.util.Map;

/** 财务统计服务 */
public interface FinanceStatService {
    Map<String, Object> getOverview(FeeBill q);

    List<Map<String, Object>> getMonthStat(FeeBill q);

    List<Map<String, Object>> getItemStat(FeeBill q);

    Map<String, Object> getAll(FeeBill q);
}
