package com.fqq.futurecommunity.community.service.impl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.constant.HttpStatus;
import com.fqq.futurecommunity.common.core.exception.CustomException;
import com.fqq.futurecommunity.community.domain.Room;
import com.fqq.futurecommunity.community.domain.Unit;
import com.fqq.futurecommunity.community.domain.dto.RoomDto;
import com.fqq.futurecommunity.community.mapper.RoomMapper;
import com.fqq.futurecommunity.community.mapper.UnitMapper;
import com.fqq.futurecommunity.community.service.RoomService;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.Date;
@Service
public class RoomServiceImpl implements RoomService {
  @Resource private RoomMapper mapper;
  @Resource private UnitMapper unitMapper;
  public Page<RoomDto> selectRoomList(Page<RoomDto> page, Room q){ return mapper.selectRoomList(page,q); }
  public Room selectRoomById(Long id){ return mapper.selectById(id); }
  public int insertRoom(Room q){ Unit unit = requireUnit(q); Long requestBuildingId = q.getBuildingId(); if(requestBuildingId != null && !requestBuildingId.equals(unit.getBuildingId())){ throw new CustomException(HttpStatus.BAD_REQUEST, "所选单元不属于当前楼栋"); } q.setBuildingId(unit.getBuildingId()); q.setCommunityId(unit.getCommunityId()); if(q.getRoomCode()==null || q.getRoomCode().isEmpty()){ q.setRoomCode("ROOM_"+System.currentTimeMillis()); } q.setCreateTime(new Date()); try{q.setCreateBy(SecurityUtils.getUserName());}catch(Exception ignored){} return mapper.insert(q); }
  public int updateRoom(Room q){ Unit unit = requireUnit(q); Long requestBuildingId = q.getBuildingId(); if(requestBuildingId != null && !requestBuildingId.equals(unit.getBuildingId())){ throw new CustomException(HttpStatus.BAD_REQUEST, "所选单元不属于当前楼栋"); } q.setBuildingId(unit.getBuildingId()); q.setCommunityId(unit.getCommunityId()); q.setUpdateTime(new Date()); try{q.setUpdateBy(SecurityUtils.getUserName());}catch(Exception ignored){} return mapper.updateById(q); }
  public int deleteRoomByIds(Long[] ids){ int r=0; for(Long id:ids){ r+=mapper.deleteById(id); } return r; }

  private Unit requireUnit(Room q) {
    if (q == null || q.getUnitId() == null) {
      throw new CustomException(HttpStatus.BAD_REQUEST, "所属单元不能为空");
    }
    Unit unit = unitMapper.selectById(q.getUnitId());
    if (unit == null) {
      throw new CustomException(HttpStatus.BAD_REQUEST, "所属单元不存在");
    }
    return unit;
  }
}
