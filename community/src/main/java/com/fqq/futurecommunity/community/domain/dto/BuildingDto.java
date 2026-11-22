package com.fqq.futurecommunity.community.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;

/** 楼栋信息列表 DTO：包含 building 表字段 + 前端展示关联字段 */
@Getter
@Setter
public class BuildingDto extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long buildingId;
    private String buildingName;
    private String buildingCode;
    private BigDecimal buildingAcreage;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;

    private String communityName;
}
