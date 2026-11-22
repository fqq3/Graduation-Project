package com.fqq.futurecommunity.community.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.Visitor;
import com.fqq.futurecommunity.community.domain.dto.VisitorDto;
public interface VisitorService {
  Page<VisitorDto> selectVisitorList(Page<VisitorDto> page, Visitor q);
  Visitor selectVisitorById(Long id);
  int insertVisitor(Visitor q);
  int updateVisitor(Visitor q);
  int deleteVisitorByIds(Long[] ids);
}
