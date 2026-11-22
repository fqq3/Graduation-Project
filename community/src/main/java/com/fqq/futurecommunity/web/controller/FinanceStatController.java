package com.fqq.futurecommunity.web.controller;

import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.community.domain.FeeBill;
import com.fqq.futurecommunity.community.service.FinanceStatService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 财务统计 Controller */
@RestController
@RequestMapping("/community/financeStat")
public class FinanceStatController {
    @Resource
    private FinanceStatService financeStatService;

    @GetMapping("/overview")
    @PreAuthorize("@pe.hasPerms('community:financeStat:list')")
    public BaseResponse overview(FeeBill q) {
        return BaseResponse.success(financeStatService.getOverview(q));
    }

    @GetMapping("/month")
    @PreAuthorize("@pe.hasPerms('community:financeStat:list')")
    public BaseResponse month(FeeBill q) {
        return BaseResponse.success(financeStatService.getMonthStat(q));
    }

    @GetMapping("/item")
    @PreAuthorize("@pe.hasPerms('community:financeStat:list')")
    public BaseResponse item(FeeBill q) {
        return BaseResponse.success(financeStatService.getItemStat(q));
    }

    @GetMapping("/all")
    @PreAuthorize("@pe.hasPerms('community:financeStat:list')")
    public BaseResponse all(FeeBill q) {
        return BaseResponse.success(financeStatService.getAll(q));
    }
}
