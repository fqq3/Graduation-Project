package com.fqq.futurecommunity.web.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.community.domain.Building;
import com.fqq.futurecommunity.community.domain.dto.BuildingDto;
import com.fqq.futurecommunity.community.service.BuildingService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;
@RestController
@RequestMapping("/system/building")
public class BuildingController extends BaseController {
  @Resource private BuildingService service;
  @GetMapping("/list") @PreAuthorize("@pe.hasPerms('system:building:list')")
  public PageResult list(Building q){ Page<BuildingDto> page=startPage(); return getData(service.selectBuildingList(page,q)); }
  @GetMapping("/{id}") @PreAuthorize("@pe.hasPerms('system:building:query')") public BaseResponse getInfo(@PathVariable Long id){ return BaseResponse.success(service.selectBuildingById(id)); }
  @PostMapping @PreAuthorize("@pe.hasPerms('system:building:add')") public BaseResponse add(@RequestBody Building q){ return toAjax(service.insertBuilding(q)); }
  @PutMapping @PreAuthorize("@pe.hasPerms('system:building:edit')") public BaseResponse edit(@RequestBody Building q){ return toAjax(service.updateBuilding(q)); }
  @DeleteMapping("/{ids}") @PreAuthorize("@pe.hasPerms('system:building:remove')") public BaseResponse remove(@PathVariable Long[] ids){ return toAjax(service.deleteBuildingByIds(ids)); }
  @GetMapping("/export") @PreAuthorize("@pe.hasPerms('system:building:export')") public ChainedMap export(Building q){ Page<BuildingDto> page = new Page<>(1, 100000); List<BuildingDto> rows = service.selectBuildingList(page, q).getRecords(); String fileName = FileStorageUtils.generateFileName("building", "xlsx"); Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName); ExcelUtils.writeExcelToFile(rows, BuildingDto.class, filePath, "Building"); return ChainedMap.create().set("code", 200).set("msg", fileName); }
  
  @GetMapping("/queryPullDown") public BaseResponse queryPullDown(Building q){ return BaseResponse.success(service.queryPullDown(q)); }
  @GetMapping("/queryPullDownRoom") public BaseResponse queryPullDownRoom(Building q){ return BaseResponse.success(service.queryPullDownRoom(q)); }
}
