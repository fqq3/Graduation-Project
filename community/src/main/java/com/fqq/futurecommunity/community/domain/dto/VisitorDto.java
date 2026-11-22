package com.fqq.futurecommunity.community.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.LocalDate;

/** 访客邀请 DTO：包含 visitor 表字段 + 前端展示关联字段 */
@Getter
@Setter
public class VisitorDto extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long visitorId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;
    private String visitorName;
    private String visitorPhoneNumber;
    private LocalDate visitorDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long createById;
    private String createByOpenId;

    private String communityName;
    private String ownerRealName;
    private String ownerPhoneNumber;
    private String ownerNickname;
    private String ownerPortrait;
}
