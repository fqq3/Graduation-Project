package com.fqq.futurecommunity.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.system.domain.SysNotice;
import com.fqq.futurecommunity.system.mapper.SysNoticeMapper;
import com.fqq.futurecommunity.system.service.SysNoticeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SysNoticeServiceImpl implements SysNoticeService {
    @Resource
    private SysNoticeMapper noticeMapper;

    @Override
    public Page<SysNotice> selectNoticeList(Page<SysNotice> page, SysNotice notice) {
        return noticeMapper.selectNoticeList(page, notice);
    }

    @Override
    public SysNotice selectNoticeById(Long noticeId) {
        return noticeMapper.selectById(noticeId);
    }

    @Override
    public int insertNotice(SysNotice notice) {
        return noticeMapper.insert(notice);
    }

    @Override
    public int updateNotice(SysNotice notice) {
        return noticeMapper.updateById(notice);
    }

    @Override
    public int deleteNoticeByIds(Long[] noticeIds) {
        int rows = 0;
        for (Long noticeId : noticeIds) {
            rows += noticeMapper.deleteById(noticeId);
        }
        return rows;
    }
}
