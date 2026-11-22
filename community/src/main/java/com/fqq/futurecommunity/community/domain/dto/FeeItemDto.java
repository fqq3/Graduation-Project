package com.fqq.futurecommunity.community.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;

/** 费用项目列表 DTO */
@Getter
@Setter
public class FeeItemDto extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long itemId;
    private String itemName;
    private String itemType;
    private BigDecimal chargeStandard;
    private String chargeUnit;
    private String chargeCycle;
    private String status;
}
