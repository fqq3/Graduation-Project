package com.fqq.futurecommunity.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.FeeBill;
import com.fqq.futurecommunity.community.domain.dto.FeeBillDto;

/** 费用账单 Service */
public interface FeeBillService {
    Page<FeeBillDto> selectFeeBillList(Page<FeeBillDto> page, FeeBill q);
    FeeBill selectFeeBillById(Long id);
    int insertFeeBill(FeeBill q);
    int updateFeeBill(FeeBill q);
    int deleteFeeBillByIds(Long[] ids);
}
