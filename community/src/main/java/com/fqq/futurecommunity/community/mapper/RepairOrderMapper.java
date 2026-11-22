package com.fqq.futurecommunity.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.RepairOrder;
import com.fqq.futurecommunity.community.domain.dto.RepairOrderDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/** 报修工单 Mapper */
@Mapper
public interface RepairOrderMapper extends BaseMapper<RepairOrder> {
    Page<RepairOrderDto> selectRepairOrderList(Page<RepairOrderDto> page, @Param("q") RepairOrder q);
}
