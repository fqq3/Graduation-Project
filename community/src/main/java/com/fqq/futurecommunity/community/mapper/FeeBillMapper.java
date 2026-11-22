package com.fqq.futurecommunity.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.FeeBill;
import com.fqq.futurecommunity.community.domain.dto.FeeBillDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/** 费用账单 Mapper */
@Mapper
public interface FeeBillMapper extends BaseMapper<FeeBill> {
    Page<FeeBillDto> selectFeeBillList(Page<FeeBillDto> page, @Param("q") FeeBill q);

    Map<String, Object> selectFinanceOverview(@Param("q") FeeBill q);

    List<Map<String, Object>> selectFinanceMonthStat(@Param("q") FeeBill q);

    List<Map<String, Object>> selectFinanceItemStat(@Param("q") FeeBill q);

}
