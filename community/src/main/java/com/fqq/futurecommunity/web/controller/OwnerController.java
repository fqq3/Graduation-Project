package com.fqq.futurecommunity.web.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.community.domain.Owner;
import com.fqq.futurecommunity.community.domain.dto.OwnerDto;
import com.fqq.futurecommunity.community.service.OwnerService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;
@RestController
@RequestMapping("/system/owner")
public class OwnerController extends BaseController {
  @Resource private OwnerService service;
  @GetMapping("/list") @PreAuthorize("@pe.hasPerms('system:owner:list')")
  public PageResult list(Owner q){ Page<OwnerDto> page=startPage(); return getData(service.selectOwnerList(page,q)); }
  @GetMapping("/{id}") @PreAuthorize("@pe.hasPerms('system:owner:query')") public BaseResponse getInfo(@PathVariable Long id){ return BaseResponse.success(service.selectOwnerById(id)); }
  @PostMapping @PreAuthorize("@pe.hasPerms('system:owner:add')") public BaseResponse add(@RequestBody Owner q){ return toAjax(service.insertOwner(q)); }
  @PutMapping @PreAuthorize("@pe.hasPerms('system:owner:edit')") public BaseResponse edit(@RequestBody Owner q){ return toAjax(service.updateOwner(q)); }
  @DeleteMapping("/{ids}") @PreAuthorize("@pe.hasPerms('system:owner:remove')") public BaseResponse remove(@PathVariable Long[] ids){ return toAjax(service.deleteOwnerByIds(ids)); }
  @GetMapping("/export") @PreAuthorize("@pe.hasPerms('system:owner:export')") public ChainedMap export(Owner q){ Page<OwnerDto> page = new Page<>(1, 100000); List<OwnerDto> rows = service.selectOwnerList(page, q).getRecords(); String fileName = FileStorageUtils.generateFileName("owner", "xlsx"); Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName); ExcelUtils.writeExcelToFile(rows, OwnerDto.class, filePath, "Owner"); return ChainedMap.create().set("code", 200).set("msg", fileName); }
  
}
