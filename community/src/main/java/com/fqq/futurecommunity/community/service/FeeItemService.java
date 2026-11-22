package com.fqq.futurecommunity.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.FeeItem;
import com.fqq.futurecommunity.community.domain.dto.FeeItemDto;

/** 费用项目 Service */
public interface FeeItemService {
    Page<FeeItemDto> selectFeeItemList(Page<FeeItemDto> page, FeeItem q);
    FeeItem selectFeeItemById(Long id);
    int insertFeeItem(FeeItem q);
    int updateFeeItem(FeeItem q);
    int deleteFeeItemByIds(Long[] ids);
}
