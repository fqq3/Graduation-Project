package com.fqq.futurecommunity.web.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.community.domain.Visitor;
import com.fqq.futurecommunity.community.domain.dto.VisitorDto;
import com.fqq.futurecommunity.community.service.VisitorService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;
@RestController
@RequestMapping("/system/visitor")
public class VisitorController extends BaseController {
  @Resource private VisitorService service;
  @GetMapping("/list") @PreAuthorize("@pe.hasPerms('system:visitor:list')")
  public PageResult list(Visitor q){ Page<VisitorDto> page=startPage(); return getData(service.selectVisitorList(page,q)); }
  @GetMapping("/{id}") public BaseResponse getInfo(@PathVariable Long id){ return BaseResponse.success(service.selectVisitorById(id)); }
  @PostMapping @PreAuthorize("@pe.hasPerms('system:visitor:add')") public BaseResponse add(@RequestBody Visitor q){ return toAjax(service.insertVisitor(q)); }
  @PutMapping @PreAuthorize("@pe.hasPerms('system:visitor:edit')") public BaseResponse edit(@RequestBody Visitor q){ return toAjax(service.updateVisitor(q)); }
  @DeleteMapping("/{ids}") @PreAuthorize("@pe.hasPerms('system:visitor:remove')") public BaseResponse remove(@PathVariable Long[] ids){ return toAjax(service.deleteVisitorByIds(ids)); }
  @GetMapping("/export") @PreAuthorize("@pe.hasPerms('system:visitor:export')") public ChainedMap export(Visitor q){ Page<VisitorDto> page = new Page<>(1, 100000); List<VisitorDto> rows = service.selectVisitorList(page, q).getRecords(); String fileName = FileStorageUtils.generateFileName("visitor", "xlsx"); Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName); ExcelUtils.writeExcelToFile(rows, VisitorDto.class, filePath, "Visitor"); return ChainedMap.create().set("code", 200).set("msg", fileName); }
  
}
