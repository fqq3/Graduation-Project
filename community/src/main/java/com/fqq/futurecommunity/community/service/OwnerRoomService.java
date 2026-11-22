package com.fqq.futurecommunity.community.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.OwnerRoom;
import com.fqq.futurecommunity.community.domain.dto.OwnerRoomDto;
public interface OwnerRoomService {
  Page<OwnerRoomDto> selectOwnerRoomList(Page<OwnerRoomDto> page, OwnerRoom q);
  OwnerRoom selectOwnerRoomById(Long id);
  int insertOwnerRoom(OwnerRoom q);
  int updateOwnerRoom(OwnerRoom q);
  int deleteOwnerRoomByIds(Long[] ids);
}
