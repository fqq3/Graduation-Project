package com.fqq.futurecommunity.community.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/** 业主审核/房屋绑定 DTO：包含 owner_room 表字段 + 前端展示关联字段 */
@Getter
@Setter
public class OwnerRoomDto extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long ownerRoomId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long buildingId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long unitId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roomId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long ownerId;
    private String ownerType;
    private String roomStatus;

    private String communityName;
    private String buildingName;
    private String unitName;
    private String roomName;
    private String roomCode;
    private String ownerRealName;
    private String ownerPhoneNumber;
    private String ownerNickname;
    private String ownerPortrait;
}
