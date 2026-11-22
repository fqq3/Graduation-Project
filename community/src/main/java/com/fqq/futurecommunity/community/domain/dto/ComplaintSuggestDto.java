package com.fqq.futurecommunity.community.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/** 投诉建议 DTO：包含 complaint_suggest 表字段 + 前端展示关联字段 */
@Getter
@Setter
public class ComplaintSuggestDto extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long complaintSuggestId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;
    private String complaintSuggestType;
    private String complaintSuggestContent;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;

    private String communityName;
    private String ownerRealName;
    private String ownerPhoneNumber;
    private String ownerNickname;
    private String ownerPortrait;
}
