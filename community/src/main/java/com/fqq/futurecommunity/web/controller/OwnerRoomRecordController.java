package com.fqq.futurecommunity.web.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.community.domain.OwnerRoomRecord;
import com.fqq.futurecommunity.community.domain.dto.OwnerRoomRecordDto;
import com.fqq.futurecommunity.community.service.OwnerRoomRecordService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;
@RestController
@RequestMapping("/system/ownerRoomRecord")
public class OwnerRoomRecordController extends BaseController {
  @Resource private OwnerRoomRecordService service;
  @GetMapping("/list") @PreAuthorize("@pe.hasPerms('system:ownerRoom:list')")
  public PageResult list(OwnerRoomRecord q){ Page<OwnerRoomRecordDto> page=startPage(); return getData(service.selectOwnerRoomRecordList(page,q)); }
  @GetMapping("/{id}") public BaseResponse getInfo(@PathVariable Long id){ return BaseResponse.success(service.selectOwnerRoomRecordById(id)); }
  @PostMapping @PreAuthorize("@pe.hasPerms('system:ownerRoomRecord:add')") public BaseResponse add(@RequestBody OwnerRoomRecord q){ return toAjax(service.insertOwnerRoomRecord(q)); }
  @PutMapping @PreAuthorize("@pe.hasPerms('system:ownerRoomRecord:edit')") public BaseResponse edit(@RequestBody OwnerRoomRecord q){ return toAjax(service.updateOwnerRoomRecord(q)); }
  @DeleteMapping("/{ids}") @PreAuthorize("@pe.hasPerms('system:ownerRoomRecord:remove')") public BaseResponse remove(@PathVariable Long[] ids){ return toAjax(service.deleteOwnerRoomRecordByIds(ids)); }
  @GetMapping("/export") @PreAuthorize("@pe.hasPerms('system:ownerRoomRecord:export')") public ChainedMap export(OwnerRoomRecord q){ Page<OwnerRoomRecordDto> page = new Page<>(1, 100000); List<OwnerRoomRecordDto> rows = service.selectOwnerRoomRecordList(page, q).getRecords(); String fileName = FileStorageUtils.generateFileName("owner_room_record", "xlsx"); Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName); ExcelUtils.writeExcelToFile(rows, OwnerRoomRecordDto.class, filePath, "OwnerRoomRecord"); return ChainedMap.create().set("code", 200).set("msg", fileName); }
  
}
