package com.fqq.futurecommunity.community.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.Room;
import com.fqq.futurecommunity.community.domain.dto.RoomDto;
public interface RoomService {
  Page<RoomDto> selectRoomList(Page<RoomDto> page, Room q);
  Room selectRoomById(Long id);
  int insertRoom(Room q);
  int updateRoom(Room q);
  int deleteRoomByIds(Long[] ids);
}
