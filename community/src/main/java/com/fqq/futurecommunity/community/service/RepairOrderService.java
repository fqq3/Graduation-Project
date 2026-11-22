package com.fqq.futurecommunity.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.RepairOrder;
import com.fqq.futurecommunity.community.domain.dto.RepairOrderDto;

/** 报修工单 Service */
public interface RepairOrderService {
    Page<RepairOrderDto> selectRepairOrderList(Page<RepairOrderDto> page, RepairOrder q);
    RepairOrder selectRepairOrderById(Long id);
    int insertRepairOrder(RepairOrder q);
    int updateRepairOrder(RepairOrder q);
    int deleteRepairOrderByIds(Long[] ids);
}
