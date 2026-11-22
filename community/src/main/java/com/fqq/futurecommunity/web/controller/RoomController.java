package com.fqq.futurecommunity.web.controller;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.community.domain.Room;
import com.fqq.futurecommunity.community.domain.dto.RoomDto;
import com.fqq.futurecommunity.community.service.RoomService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;
@RestController
@RequestMapping("/system/room")
public class RoomController extends BaseController {
  @Resource private RoomService service;
  @GetMapping("/list") @PreAuthorize("@pe.hasPerms('system:room:list')")
  public PageResult list(Room q){ Page<RoomDto> page=startPage(); return getData(service.selectRoomList(page,q)); }
  @GetMapping("/{id}") @PreAuthorize("@pe.hasPerms('system:room:query')") public BaseResponse getInfo(@PathVariable Long id){ return BaseResponse.success(service.selectRoomById(id)); }
  @PostMapping @PreAuthorize("@pe.hasPerms('system:room:add')") public BaseResponse add(@RequestBody Room q){ return toAjax(service.insertRoom(q)); }
  @PutMapping @PreAuthorize("@pe.hasPerms('system:room:edit')") public BaseResponse edit(@RequestBody Room q){ return toAjax(service.updateRoom(q)); }
  @DeleteMapping("/{ids}") @PreAuthorize("@pe.hasPerms('system:room:remove')") public BaseResponse remove(@PathVariable Long[] ids){ return toAjax(service.deleteRoomByIds(ids)); }
  @GetMapping("/export") @PreAuthorize("@pe.hasPerms('system:room:export')") public ChainedMap export(Room q){ Page<RoomDto> page = new Page<>(1, 100000); List<RoomDto> rows = service.selectRoomList(page, q).getRecords(); String fileName = FileStorageUtils.generateFileName("room", "xlsx"); Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName); ExcelUtils.writeExcelToFile(rows, RoomDto.class, filePath, "Room"); return ChainedMap.create().set("code", 200).set("msg", fileName); }
  
}
