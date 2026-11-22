package com.fqq.futurecommunity.community.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.OwnerRoomRecord;
import com.fqq.futurecommunity.community.domain.dto.OwnerRoomRecordDto;
public interface OwnerRoomRecordService {
  Page<OwnerRoomRecordDto> selectOwnerRoomRecordList(Page<OwnerRoomRecordDto> page, OwnerRoomRecord q);
  OwnerRoomRecord selectOwnerRoomRecordById(Long id);
  int insertOwnerRoomRecord(OwnerRoomRecord q);
  int updateOwnerRoomRecord(OwnerRoomRecord q);
  int deleteOwnerRoomRecordByIds(Long[] ids);
}
