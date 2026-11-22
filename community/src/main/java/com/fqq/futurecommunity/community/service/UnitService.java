package com.fqq.futurecommunity.community.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.Unit;
import com.fqq.futurecommunity.community.domain.dto.UnitDto;
public interface UnitService {
  Page<UnitDto> selectUnitList(Page<UnitDto> page, Unit q);
  Unit selectUnitById(Long id);
  int insertUnit(Unit q);
  int updateUnit(Unit q);
  int deleteUnitByIds(Long[] ids);
}
