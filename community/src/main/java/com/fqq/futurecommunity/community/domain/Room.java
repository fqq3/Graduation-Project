package com.fqq.futurecommunity.community.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Room */
@Getter
@Setter
@Accessors(chain = true)
@TableName("room")
public class Room extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "room_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roomId;

    @TableField("community_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;

    @TableField("building_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long buildingId;

    @TableField("unit_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long unitId;

    @TableField("room_level")
    private Integer roomLevel;

    @TableField("room_code")
    private String roomCode;

    @TableField("room_name")
    private String roomName;

    @TableField("room_acreage")
    private BigDecimal roomAcreage;

    @TableField("room_cost")
    private BigDecimal roomCost;

    @TableField("room_status")
    private String roomStatus;

    @TableField("room_is_shop")
    private String roomIsShop;

    @TableField("room_s_commercial_house")
    private String roomSCommercialHouse;

    @TableField("room_house_type")
    private String roomHouseType;
}
