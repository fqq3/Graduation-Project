package com.fqq.futurecommunity.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.constant.HttpStatus;
import com.fqq.futurecommunity.common.core.exception.CustomException;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.community.domain.FeeItem;
import com.fqq.futurecommunity.community.domain.dto.FeeItemDto;
import com.fqq.futurecommunity.community.mapper.FeeItemMapper;
import com.fqq.futurecommunity.community.service.FeeItemService;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/** 费用项目 ServiceImpl */
@Service
public class FeeItemServiceImpl implements FeeItemService {
    @Resource
    private FeeItemMapper feeItemMapper;

    @Override
    public Page<FeeItemDto> selectFeeItemList(Page<FeeItemDto> page, FeeItem q) {
        return feeItemMapper.selectFeeItemList(page, q);
    }

    @Override
    public FeeItem selectFeeItemById(Long id) {
        return feeItemMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFeeItem(FeeItem q) {
        if (q.getStatus() == null || q.getStatus().isBlank()) {
            q.setStatus("0");
        }
        fillCreate(q);
        int rows = feeItemMapper.insert(q);

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFeeItem(FeeItem q) {

        fillUpdate(q);
        return feeItemMapper.updateById(q);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFeeItemByIds(Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            rows += feeItemMapper.deleteById(id);
        }
        return rows;
    }

    private void fillCreate(FeeItem q) {
        Date now = new Date();
        q.setCreateTime(now);
        q.setUpdateTime(now);
        try {
            String username = SecurityUtils.getUserName();
            q.setCreateBy(username);
            q.setUpdateBy(username);
        } catch (Exception ignored) {
            q.setCreateBy("admin");
            q.setUpdateBy("admin");
        }
    }

    private void fillUpdate(FeeItem q) {
        q.setUpdateTime(new Date());
        try {
            q.setUpdateBy(SecurityUtils.getUserName());
        } catch (Exception ignored) {
            q.setUpdateBy("admin");
        }
    }


}
