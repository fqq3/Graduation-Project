package com.fqq.futurecommunity.community.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;

/** 单元信息列表 DTO：包含 unit 表字段 + 前端展示关联字段 */
@Getter
@Setter
public class UnitDto extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long unitId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long buildingId;
    private String unitName;
    private String unitCode;
    private Integer unitLevel;
    private BigDecimal unitAcreage;
    private String unitHaveElevator;

    private String communityName;
    private String buildingName;
}
