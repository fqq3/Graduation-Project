package com.fqq.futurecommunity.community.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: fqq
 * @Date: 2026/3/21 - 03 - 21 - 2:50
 * @Description: com.fqq.futurecommunity.community.domain.vo
 * @version: 1.0
 */
@Setter
@Getter
public class CommunityVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    //小区id
    private Long communityId;
    //小区名称
    private String communityName;
}
