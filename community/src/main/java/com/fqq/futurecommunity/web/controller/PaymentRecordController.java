package com.fqq.futurecommunity.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.community.domain.PaymentRecord;
import com.fqq.futurecommunity.community.domain.dto.PaymentRecordDto;
import com.fqq.futurecommunity.community.service.PaymentRecordService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;

/** 缴费记录 Controller */
@RestController
@RequestMapping("/community/paymentRecord")
public class PaymentRecordController extends BaseController {
    @Resource
    private PaymentRecordService paymentRecordService;

    @GetMapping("/list")
    @PreAuthorize("@pe.hasPerms('community:paymentRecord:list')")
    public PageResult list(PaymentRecord q) {
        Page<PaymentRecordDto> page = startPage();
        return getData(paymentRecordService.selectPaymentRecordList(page, q));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@pe.hasPerms('community:paymentRecord:query')")
    public BaseResponse getInfo(@PathVariable Long id) {
        return BaseResponse.success(paymentRecordService.selectPaymentRecordById(id));
    }

    @PostMapping
    @PreAuthorize("@pe.hasPerms('community:paymentRecord:add')")
    public BaseResponse add(@RequestBody PaymentRecord q) {
        return toAjax(paymentRecordService.insertPaymentRecord(q));
    }

    @PutMapping
    @PreAuthorize("@pe.hasPerms('community:paymentRecord:edit')")
    public BaseResponse edit(@RequestBody PaymentRecord q) {
        return toAjax(paymentRecordService.updatePaymentRecord(q));
    }

    @DeleteMapping("/{ids}")
    @PreAuthorize("@pe.hasPerms('community:paymentRecord:remove')")
    public BaseResponse remove(@PathVariable Long[] ids) {
        return toAjax(paymentRecordService.deletePaymentRecordByIds(ids));
    }

    @GetMapping("/export")
    @PreAuthorize("@pe.hasPerms('community:paymentRecord:export')")
    public ChainedMap export(PaymentRecord q) {
        Page<PaymentRecordDto> page = new Page<>(1, 100000);
        List<PaymentRecordDto> rows = paymentRecordService.selectPaymentRecordList(page, q).getRecords();
        String fileName = FileStorageUtils.generateFileName("payment_record", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(rows, PaymentRecordDto.class, filePath, "PaymentRecord");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }
}
