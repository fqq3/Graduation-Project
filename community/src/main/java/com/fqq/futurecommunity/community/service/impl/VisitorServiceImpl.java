package com.fqq.futurecommunity.community.service.impl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.Visitor;
import com.fqq.futurecommunity.community.domain.dto.VisitorDto;
import com.fqq.futurecommunity.community.mapper.VisitorMapper;
import com.fqq.futurecommunity.community.service.VisitorService;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.Date;
@Service
public class VisitorServiceImpl implements VisitorService {
  @Resource private VisitorMapper mapper;
  public Page<VisitorDto> selectVisitorList(Page<VisitorDto> page, Visitor q){ return mapper.selectVisitorList(page,q); }
  public Visitor selectVisitorById(Long id){ return mapper.selectById(id); }
  public int insertVisitor(Visitor q){ q.setCreateTime(new Date()); try{q.setCreateBy(SecurityUtils.getUserName());}catch(Exception ignored){} return mapper.insert(q); }
  public int updateVisitor(Visitor q){ q.setUpdateTime(new Date()); try{q.setUpdateBy(SecurityUtils.getUserName());}catch(Exception ignored){} return mapper.updateById(q); }
  public int deleteVisitorByIds(Long[] ids){ int r=0; for(Long id:ids){ r+=mapper.deleteById(id); } return r; }
  
}
