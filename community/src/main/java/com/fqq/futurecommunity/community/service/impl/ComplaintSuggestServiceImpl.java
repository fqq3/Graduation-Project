package com.fqq.futurecommunity.community.service.impl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.ComplaintSuggest;
import com.fqq.futurecommunity.community.domain.dto.ComplaintSuggestDto;
import com.fqq.futurecommunity.community.mapper.ComplaintSuggestMapper;
import com.fqq.futurecommunity.community.service.ComplaintSuggestService;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.Date;
@Service
public class ComplaintSuggestServiceImpl implements ComplaintSuggestService {
  @Resource private ComplaintSuggestMapper mapper;
  public Page<ComplaintSuggestDto> selectComplaintSuggestList(Page<ComplaintSuggestDto> page, ComplaintSuggest q){ return mapper.selectComplaintSuggestList(page,q); }
  public ComplaintSuggest selectComplaintSuggestById(Long id){ return mapper.selectById(id); }
  public int insertComplaintSuggest(ComplaintSuggest q){ q.setCreateTime(new Date()); try{q.setCreateBy(SecurityUtils.getUserName());}catch(Exception ignored){} return mapper.insert(q); }
  public int updateComplaintSuggest(ComplaintSuggest q){ q.setUpdateTime(new Date()); try{q.setUpdateBy(SecurityUtils.getUserName());}catch(Exception ignored){} return mapper.updateById(q); }
  public int deleteComplaintSuggestByIds(Long[] ids){ int r=0; for(Long id:ids){ r+=mapper.deleteById(id); } return r; }
  
}
