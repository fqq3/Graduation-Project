package com.fqq.futurecommunity.web.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.system.domain.SysNotice;
import com.fqq.futurecommunity.system.service.SysNoticeService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/system/notice")
public class SysNoticeController extends BaseController {
    @Resource
    private SysNoticeService noticeService;

    @GetMapping("/list")
    @PreAuthorize("@pe.hasPerms('system:notice:list')")
    public PageResult<SysNotice> list(SysNotice notice) {
        Page<SysNotice> page = startPage();
        return getData(noticeService.selectNoticeList(page, notice));
    }

    @GetMapping("/{noticeId}")
    @PreAuthorize("@pe.hasPerms('system:notice:query')")
    public BaseResponse<SysNotice> getInfo(@PathVariable Long noticeId) {
        return BaseResponse.success(noticeService.selectNoticeById(noticeId));
    }

    @PostMapping
    @PreAuthorize("@pe.hasPerms('system:notice:add')")
    public BaseResponse<Void> add(@RequestBody SysNotice notice) {
        try { notice.setCreateBy(SecurityUtils.getUserName()); } catch (Exception ignored) {}
        notice.setCreateTime(new Date());
        return toAjax(noticeService.insertNotice(notice));
    }

    @PutMapping
    @PreAuthorize("@pe.hasPerms('system:notice:edit')")
    public BaseResponse<Void> edit(@RequestBody SysNotice notice) {
        try { notice.setUpdateBy(SecurityUtils.getUserName()); } catch (Exception ignored) {}
        notice.setUpdateTime(new Date());
        return toAjax(noticeService.updateNotice(notice));
    }

    @DeleteMapping("/{noticeIds}")
    @PreAuthorize("@pe.hasPerms('system:notice:remove')")
    public BaseResponse<Void> remove(@PathVariable Long[] noticeIds) {
        return toAjax(noticeService.deleteNoticeByIds(noticeIds));
    }
}
