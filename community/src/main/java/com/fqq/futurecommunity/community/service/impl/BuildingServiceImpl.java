package com.fqq.futurecommunity.community.service.impl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.constant.HttpStatus;
import com.fqq.futurecommunity.common.core.exception.CustomException;
import com.fqq.futurecommunity.community.domain.Building;
import com.fqq.futurecommunity.community.domain.dto.BuildingDto;
import com.fqq.futurecommunity.community.mapper.BuildingMapper;
import com.fqq.futurecommunity.community.service.BuildingService;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.Date;
@Service
public class BuildingServiceImpl implements BuildingService {
  @Resource private BuildingMapper mapper;
  public Page<BuildingDto> selectBuildingList(Page<BuildingDto> page, Building q){ return mapper.selectBuildingList(page,q); }
  public Building selectBuildingById(Long id){ return mapper.selectById(id); }
  public int insertBuilding(Building q){ validateCommunityId(q); if(q.getBuildingCode()==null || q.getBuildingCode().isEmpty()){ q.setBuildingCode("BUILDING_"+System.currentTimeMillis()); } q.setCreateTime(new Date()); try{q.setCreateBy(SecurityUtils.getUserName());}catch(Exception ignored){} return mapper.insert(q); }
  public int updateBuilding(Building q){ validateCommunityId(q); q.setUpdateTime(new Date()); try{q.setUpdateBy(SecurityUtils.getUserName());}catch(Exception ignored){} return mapper.updateById(q); }
  public int deleteBuildingByIds(Long[] ids){ int r=0; for(Long id:ids){ r+=mapper.deleteById(id); } return r; }
  public java.util.List<com.fqq.futurecommunity.community.domain.vo.BuildingVo> queryPullDown(Building q){ return mapper.selectBuildingOptions(q); }
public java.util.List<com.fqq.futurecommunity.community.domain.vo.BuildingVo> queryPullDownRoom(Building q){
  java.util.List<com.fqq.futurecommunity.community.domain.vo.BuildingVo> list=mapper.selectBuildingOptions(q);
  for (com.fqq.futurecommunity.community.domain.vo.BuildingVo b:list){ b.setChildren(mapper.selectUnitOptions(b.getBuildingId())); }
  return list;
}

  private void validateCommunityId(Building q) {
    if (q == null || q.getCommunityId() == null) {
      throw new CustomException(HttpStatus.BAD_REQUEST, "所属小区不能为空");
    }
  }

}
