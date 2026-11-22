package com.fqq.futurecommunity.community.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.Building;
import com.fqq.futurecommunity.community.domain.dto.BuildingDto;
public interface BuildingService {
  Page<BuildingDto> selectBuildingList(Page<BuildingDto> page, Building q);
  Building selectBuildingById(Long id);
  int insertBuilding(Building q);
  int updateBuilding(Building q);
  int deleteBuildingByIds(Long[] ids);
 java.util.List<com.fqq.futurecommunity.community.domain.vo.BuildingVo> queryPullDown(Building q);
 java.util.List<com.fqq.futurecommunity.community.domain.vo.BuildingVo> queryPullDownRoom(Building q);
}
