package com.fqq.futurecommunity.community.service.impl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.constant.HttpStatus;
import com.fqq.futurecommunity.common.core.exception.CustomException;
import com.fqq.futurecommunity.community.domain.Building;
import com.fqq.futurecommunity.community.domain.Unit;
import com.fqq.futurecommunity.community.domain.dto.UnitDto;
import com.fqq.futurecommunity.community.mapper.BuildingMapper;
import com.fqq.futurecommunity.community.mapper.UnitMapper;
import com.fqq.futurecommunity.community.service.UnitService;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.Date;
@Service
public class UnitServiceImpl implements UnitService {
  @Resource private UnitMapper mapper;
  @Resource private BuildingMapper buildingMapper;
  public Page<UnitDto> selectUnitList(Page<UnitDto> page, Unit q){ return mapper.selectUnitList(page,q); }
  public Unit selectUnitById(Long id){ return mapper.selectById(id); }
  public int insertUnit(Unit q){ Building building = requireBuilding(q); q.setCommunityId(building.getCommunityId()); if(q.getUnitCode()==null || q.getUnitCode().isEmpty()){ q.setUnitCode("UNIT_"+System.currentTimeMillis()); } q.setCreateTime(new Date()); try{q.setCreateBy(SecurityUtils.getUserName());}catch(Exception ignored){} return mapper.insert(q); }
  public int updateUnit(Unit q){ Building building = requireBuilding(q); q.setCommunityId(building.getCommunityId()); q.setUpdateTime(new Date()); try{q.setUpdateBy(SecurityUtils.getUserName());}catch(Exception ignored){} return mapper.updateById(q); }
  public int deleteUnitByIds(Long[] ids){ int r=0; for(Long id:ids){ r+=mapper.deleteById(id); } return r; }

  private Building requireBuilding(Unit q) {
    if (q == null || q.getBuildingId() == null) {
      throw new CustomException(HttpStatus.BAD_REQUEST, "所属楼栋不能为空");
    }
    Building building = buildingMapper.selectById(q.getBuildingId());
    if (building == null) {
      throw new CustomException(HttpStatus.BAD_REQUEST, "所属楼栋不存在");
    }
    return building;
  }
}
