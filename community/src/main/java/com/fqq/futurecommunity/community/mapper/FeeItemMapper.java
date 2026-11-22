package com.fqq.futurecommunity.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.FeeItem;
import com.fqq.futurecommunity.community.domain.dto.FeeItemDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/** 费用项目 Mapper */
@Mapper
public interface FeeItemMapper extends BaseMapper<FeeItem> {
    Page<FeeItemDto> selectFeeItemList(Page<FeeItemDto> page, @Param("q") FeeItem q);
}
