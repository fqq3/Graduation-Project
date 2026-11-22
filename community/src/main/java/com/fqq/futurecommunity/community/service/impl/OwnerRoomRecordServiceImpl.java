package com.fqq.futurecommunity.community.service.impl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.OwnerRoomRecord;
import com.fqq.futurecommunity.community.domain.dto.OwnerRoomRecordDto;
import com.fqq.futurecommunity.community.mapper.OwnerRoomRecordMapper;
import com.fqq.futurecommunity.community.service.OwnerRoomRecordService;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.Date;
@Service
public class OwnerRoomRecordServiceImpl implements OwnerRoomRecordService {
  @Resource private OwnerRoomRecordMapper mapper;
  public Page<OwnerRoomRecordDto> selectOwnerRoomRecordList(Page<OwnerRoomRecordDto> page, OwnerRoomRecord q){ return mapper.selectOwnerRoomRecordList(page,q); }
  public OwnerRoomRecord selectOwnerRoomRecordById(Long id){ return mapper.selectById(id); }
  public int insertOwnerRoomRecord(OwnerRoomRecord q){ q.setCreateTime(new Date()); try{q.setCreateBy(SecurityUtils.getUserName());}catch(Exception ignored){} return mapper.insert(q); }
  public int updateOwnerRoomRecord(OwnerRoomRecord q){ q.setUpdateTime(new Date()); try{q.setUpdateBy(SecurityUtils.getUserName());}catch(Exception ignored){} return mapper.updateById(q); }
  public int deleteOwnerRoomRecordByIds(Long[] ids){ int r=0; for(Long id:ids){ r+=mapper.deleteById(id); } return r; }
  
}
