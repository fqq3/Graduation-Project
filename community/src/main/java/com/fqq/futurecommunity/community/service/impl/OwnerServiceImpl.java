package com.fqq.futurecommunity.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.community.domain.Owner;
import com.fqq.futurecommunity.community.domain.dto.OwnerDto;
import com.fqq.futurecommunity.community.mapper.OwnerMapper;
import com.fqq.futurecommunity.community.service.OwnerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OwnerServiceImpl implements OwnerService {
  @Resource private OwnerMapper mapper;

  public Page<OwnerDto> selectOwnerList(Page<OwnerDto> page, Owner q){ return mapper.selectOwnerList(page,q); }
  public Owner selectOwnerById(Long id){ return mapper.selectById(id); }

  public int insertOwner(Owner q){
    Date now = new Date();
    q.setCreateTime(now);
    try{ q.setCreateBy(SecurityUtils.getUserName()); }catch(Exception ignored){}
    return mapper.insert(q);
  }

  public int updateOwner(Owner q){ q.setUpdateTime(new Date()); try{q.setUpdateBy(SecurityUtils.getUserName());}catch(Exception ignored){} return mapper.updateById(q); }
  public int deleteOwnerByIds(Long[] ids){ int r=0; for(Long id:ids){ r+=mapper.deleteById(id); } return r; }
}
