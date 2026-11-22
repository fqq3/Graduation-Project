package com.fqq.futurecommunity.common.utils;

import com.fqq.futurecommunity.community.domain.dto.CommunityDto;
import com.fqq.futurecommunity.community.domain.vo.CommunityVo;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author: fqq
 * @Date: 2026/3/21 - 03 - 21 - 3:51
 * @Description: com.fqq.futurecommunity.common.utils
 * @version: 1.0
 */

@Mapper(componentModel = "spring")
public interface CommunityConvert {

    /**
     * DTO → VO
     */
    CommunityVo toVo(CommunityDto dto);

    /**
     * List转换
     */
    List<CommunityVo> toVoList(List<CommunityDto> list);

}
