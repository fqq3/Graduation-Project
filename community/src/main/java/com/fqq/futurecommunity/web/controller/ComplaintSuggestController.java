package com.fqq.futurecommunity.web.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.community.domain.ComplaintSuggest;
import com.fqq.futurecommunity.community.domain.dto.ComplaintSuggestDto;
import com.fqq.futurecommunity.community.service.ComplaintSuggestService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;
@RestController
@RequestMapping("/system/suggest")
public class ComplaintSuggestController extends BaseController {
  @Resource private ComplaintSuggestService service;
  @GetMapping("/list") @PreAuthorize("@pe.hasPerms('system:suggest:list')")
  public PageResult list(ComplaintSuggest q){ Page<ComplaintSuggestDto> page=startPage(); return getData(service.selectComplaintSuggestList(page,q)); }
  @GetMapping("/{id}") public BaseResponse getInfo(@PathVariable Long id){ return BaseResponse.success(service.selectComplaintSuggestById(id)); }
  @PostMapping @PreAuthorize("@pe.hasPerms('system:suggest:add')") public BaseResponse add(@RequestBody ComplaintSuggest q){ return toAjax(service.insertComplaintSuggest(q)); }
  @PutMapping @PreAuthorize("@pe.hasPerms('system:suggest:edit')") public BaseResponse edit(@RequestBody ComplaintSuggest q){ return toAjax(service.updateComplaintSuggest(q)); }
  @DeleteMapping("/{ids}") @PreAuthorize("@pe.hasPerms('system:suggest:remove')") public BaseResponse remove(@PathVariable Long[] ids){ return toAjax(service.deleteComplaintSuggestByIds(ids)); }
  @GetMapping("/export") @PreAuthorize("@pe.hasPerms('system:suggest:export')") public ChainedMap export(ComplaintSuggest q){ Page<ComplaintSuggestDto> page = new Page<>(1, 100000); List<ComplaintSuggestDto> rows = service.selectComplaintSuggestList(page, q).getRecords(); String fileName = FileStorageUtils.generateFileName("complaint_suggest", "xlsx"); Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName); ExcelUtils.writeExcelToFile(rows, ComplaintSuggestDto.class, filePath, "ComplaintSuggest"); return ChainedMap.create().set("code", 200).set("msg", fileName); }
  
}
