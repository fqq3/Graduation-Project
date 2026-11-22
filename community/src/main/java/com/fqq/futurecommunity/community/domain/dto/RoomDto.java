package com.fqq.futurecommunity.community.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;

/** 房屋信息列表 DTO：包含 room 表字段 + 前端展示关联字段 */
@Getter
@Setter
public class RoomDto extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roomId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long buildingId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long unitId;
    private Integer roomLevel;
    private String roomCode;
    private String roomName;
    private BigDecimal roomAcreage;
    private BigDecimal roomCost;
    private String roomStatus;
    private String roomIsShop;
    private String roomSCommercialHouse;
    private String roomHouseType;

    private String communityName;
    private String buildingName;
    private String unitName;
}
