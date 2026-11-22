package com.fqq.futurecommunity.community.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.Owner;
import com.fqq.futurecommunity.community.domain.dto.OwnerDto;
public interface OwnerService {
  Page<OwnerDto> selectOwnerList(Page<OwnerDto> page, Owner q);
  Owner selectOwnerById(Long id);
  int insertOwner(Owner q);
  int updateOwner(Owner q);
  int deleteOwnerByIds(Long[] ids);
}
