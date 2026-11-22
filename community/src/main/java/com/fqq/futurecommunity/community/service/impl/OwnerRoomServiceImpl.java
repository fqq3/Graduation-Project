package com.fqq.futurecommunity.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.OwnerRoomRecord;
import com.fqq.futurecommunity.community.domain.OwnerRoom;
import com.fqq.futurecommunity.community.domain.dto.OwnerRoomDto;
import com.fqq.futurecommunity.community.mapper.OwnerRoomRecordMapper;
import com.fqq.futurecommunity.community.mapper.OwnerRoomMapper;
import com.fqq.futurecommunity.community.service.OwnerRoomService;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OwnerRoomServiceImpl implements OwnerRoomService {
  @Resource private OwnerRoomMapper mapper;
  @Resource private OwnerRoomRecordMapper ownerRoomRecordMapper;

  public Page<OwnerRoomDto> selectOwnerRoomList(Page<OwnerRoomDto> page, OwnerRoom q){ return mapper.selectOwnerRoomList(page,q); }
  public OwnerRoom selectOwnerRoomById(Long id){ return mapper.selectById(id); }
  public int insertOwnerRoom(OwnerRoom q){
    if(q.getRoomStatus()==null || q.getRoomStatus().isBlank()){ q.setRoomStatus("Auditing"); }
    Date now = new Date();
    q.setCreateTime(now);
    q.setUpdateTime(now);
    try{
      String username = SecurityUtils.getUserName();
      q.setCreateBy(username);
      q.setUpdateBy(username);
    }catch(Exception ignored){}
    return mapper.insert(q);
  }
  public int updateOwnerRoom(OwnerRoom q){
    OwnerRoom current = mapper.selectById(q.getOwnerRoomId());
    q.setUpdateTime(new Date());
    try{q.setUpdateBy(SecurityUtils.getUserName());}catch(Exception ignored){}
    int rows = mapper.updateById(q);
    boolean shouldInsertAuditRecord =
            current != null &&
            (
                    (q.getRoomStatus() != null && !q.getRoomStatus().isBlank() && !q.getRoomStatus().equals(current.getRoomStatus())) ||
                    (q.getRecordAuditOpinion() != null && !q.getRecordAuditOpinion().isBlank()) ||
                    (q.getRecordAuditType() != null && !q.getRecordAuditType().isBlank())
            );
    if (shouldInsertAuditRecord) {
      insertAuditRecord(current, q);
    }
    return rows;
  }
  public int deleteOwnerRoomByIds(Long[] ids){ int r=0; for(Long id:ids){ r+=mapper.deleteById(id); } return r; }

  private void insertAuditRecord(OwnerRoom current, OwnerRoom updateData) {
    OwnerRoomRecord record = new OwnerRoomRecord();
    record.setOwnerRoomId(String.valueOf(current.getOwnerRoomId()));
    record.setCommunityId(current.getCommunityId());
    record.setBuildingId(current.getBuildingId());
    record.setUnitId(current.getUnitId());
    record.setRoomId(current.getRoomId());
    record.setOwnerId(current.getOwnerId());
    record.setOwnerType(updateData.getOwnerType() != null ? updateData.getOwnerType() : current.getOwnerType());
    record.setRoomStatus(updateData.getRoomStatus());
    record.setRecordAuditOpinion(updateData.getRecordAuditOpinion());
    record.setRecordAuditType(updateData.getRecordAuditType() != null && !updateData.getRecordAuditType().isBlank() ? updateData.getRecordAuditType() : "Web");
    record.setRemark(updateData.getRemark());
    record.setCreateTime(new Date());
    record.setUpdateTime(record.getCreateTime());
    try{
      String username = SecurityUtils.getUserName();
      record.setCreateBy(username);
      record.setUpdateBy(username);
    }catch(Exception ignored){}
    ownerRoomRecordMapper.insert(record);
  }
}
