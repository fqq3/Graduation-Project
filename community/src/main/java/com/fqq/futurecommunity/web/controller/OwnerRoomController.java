package com.fqq.futurecommunity.web.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.community.domain.OwnerRoom;
import com.fqq.futurecommunity.community.domain.dto.OwnerRoomDto;
import com.fqq.futurecommunity.community.service.OwnerRoomService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;
@RestController
@RequestMapping("/system/ownerRoom")
public class OwnerRoomController extends BaseController {
  @Resource private OwnerRoomService service;
  @GetMapping("/list") @PreAuthorize("@pe.hasPerms('system:ownerRoom:list')")
  public PageResult list(OwnerRoom q){ Page<OwnerRoomDto> page=startPage(); return getData(service.selectOwnerRoomList(page,q)); }
  @GetMapping("/{id}") public BaseResponse getInfo(@PathVariable Long id){ return BaseResponse.success(service.selectOwnerRoomById(id)); }
  @PostMapping @PreAuthorize("@pe.hasPerms('system:ownerRoom:add')") public BaseResponse add(@RequestBody OwnerRoom q){ return toAjax(service.insertOwnerRoom(q)); }
  @PutMapping @PreAuthorize("@pe.hasPerms('system:ownerRoom:edit')") public BaseResponse edit(@RequestBody OwnerRoom q){ return toAjax(service.updateOwnerRoom(q)); }
  @DeleteMapping("/{ids}") @PreAuthorize("@pe.hasPerms('system:ownerRoom:remove')") public BaseResponse remove(@PathVariable Long[] ids){ return toAjax(service.deleteOwnerRoomByIds(ids)); }
  @GetMapping("/export") @PreAuthorize("@pe.hasPerms('system:ownerRoom:export')") public ChainedMap export(OwnerRoom q){ Page<OwnerRoomDto> page = new Page<>(1, 100000); List<OwnerRoomDto> rows = service.selectOwnerRoomList(page, q).getRecords(); String fileName = FileStorageUtils.generateFileName("owner_room", "xlsx"); Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName); ExcelUtils.writeExcelToFile(rows, OwnerRoomDto.class, filePath, "OwnerRoom"); return ChainedMap.create().set("code", 200).set("msg", fileName); }
  
}
