package com.fqq.futurecommunity.community.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.ComplaintSuggest;
import com.fqq.futurecommunity.community.domain.dto.ComplaintSuggestDto;
public interface ComplaintSuggestService {
  Page<ComplaintSuggestDto> selectComplaintSuggestList(Page<ComplaintSuggestDto> page, ComplaintSuggest q);
  ComplaintSuggest selectComplaintSuggestById(Long id);
  int insertComplaintSuggest(ComplaintSuggest q);
  int updateComplaintSuggest(ComplaintSuggest q);
  int deleteComplaintSuggestByIds(Long[] ids);
}
