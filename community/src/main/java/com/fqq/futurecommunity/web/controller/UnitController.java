package com.fqq.futurecommunity.web.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.community.domain.Unit;
import com.fqq.futurecommunity.community.domain.dto.UnitDto;
import com.fqq.futurecommunity.community.service.UnitService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;
@RestController
@RequestMapping("/system/unit")
public class UnitController extends BaseController {
  @Resource private UnitService service;
  @GetMapping("/list") @PreAuthorize("@pe.hasPerms('system:unit:list')")
  public PageResult list(Unit q){ Page<UnitDto> page=startPage(); return getData(service.selectUnitList(page,q)); }
  @GetMapping("/{id}") @PreAuthorize("@pe.hasPerms('system:unit:query')") public BaseResponse getInfo(@PathVariable Long id){ return BaseResponse.success(service.selectUnitById(id)); }
  @PostMapping @PreAuthorize("@pe.hasPerms('system:unit:add')") public BaseResponse add(@RequestBody Unit q){ return toAjax(service.insertUnit(q)); }
  @PutMapping @PreAuthorize("@pe.hasPerms('system:unit:edit')") public BaseResponse edit(@RequestBody Unit q){ return toAjax(service.updateUnit(q)); }
  @DeleteMapping("/{ids}") @PreAuthorize("@pe.hasPerms('system:unit:remove')") public BaseResponse remove(@PathVariable Long[] ids){ return toAjax(service.deleteUnitByIds(ids)); }
  @GetMapping("/export") @PreAuthorize("@pe.hasPerms('system:unit:export')") public ChainedMap export(Unit q){ Page<UnitDto> page = new Page<>(1, 100000); List<UnitDto> rows = service.selectUnitList(page, q).getRecords(); String fileName = FileStorageUtils.generateFileName("unit", "xlsx"); Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName); ExcelUtils.writeExcelToFile(rows, UnitDto.class, filePath, "Unit"); return ChainedMap.create().set("code", 200).set("msg", fileName); }
  
}
