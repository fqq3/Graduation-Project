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

/** OwnerRoom */
@Getter
@Setter
@Accessors(chain = true)
@TableName("owner_room")
public class OwnerRoom extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "owner_room_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long ownerRoomId;

    @TableField("community_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;

    @TableField("building_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long buildingId;

    @TableField("unit_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long unitId;

    @TableField("room_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roomId;

    @TableField("owner_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long ownerId;

    @TableField("owner_type")
    private String ownerType;

    @TableField("room_status")
    private String roomStatus;

    @TableField(exist = false)
    private String recordAuditOpinion;

    @TableField(exist = false)
    private String recordAuditType;
}
