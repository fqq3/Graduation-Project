package com.fqq.futurecommunity.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.system.domain.SysNotice;

public interface SysNoticeService {
    Page<SysNotice> selectNoticeList(Page<SysNotice> page, SysNotice notice);
    SysNotice selectNoticeById(Long noticeId);
    int insertNotice(SysNotice notice);
    int updateNotice(SysNotice notice);
    int deleteNoticeByIds(Long[] noticeIds);
}
