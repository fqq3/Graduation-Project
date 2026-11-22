package com.fqq.futurecommunity.community.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class OwnerRoomRecordDto extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long recordId;
    private String ownerRoomId;
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
    private String recordAuditOpinion;
    private String recordAuditType;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long createById;
    private String createByName;

    private String communityName;
    private String buildingName;
    private String unitName;
    private String roomName;
    private String ownerRealName;
    private String ownerPhoneNumber;
    private String ownerNickname;
    private String ownerPortrait;
}
