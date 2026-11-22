package com.fqq.futurecommunity.community.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.LocalDate;

/**
 * 业主信息列表 DTO。
 *
 * 说明：
 * 1. 包含 owner 表自身字段；
 * 2. 额外补充前端业主列表需要展示的房屋关联字段：小区、楼栋、单元、房间；
 * 3. 不继承 Owner，避免 Lombok 链式 setter 与实体类冲突。
 */
@Getter
@Setter
public class OwnerDto extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long ownerId;
    private String ownerNickname;
    private String ownerRealName;
    private String ownerGender;
    private Integer ownerAge;
    private String ownerIdCard;
    private String ownerPhoneNumber;
    private String ownerOpenId;
    private String ownerWechatId;
    private String ownerQqNumber;
    private LocalDate ownerBirthday;
    private String ownerPortrait;
    private String ownerSignature;
    private String ownerStatus;
    private String ownerLogonMode;
    private String ownerType;
    private String ownerPassword;

    /** 业主当前/最近绑定房屋关系 ID */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long ownerRoomId;

    /** 小区信息 */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;
    private String communityName;

    /** 楼栋信息 */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long buildingId;
    private String buildingName;

    /** 单元信息 */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long unitId;
    private String unitName;

    /** 房间信息 */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roomId;
    private String roomName;

    /** 房屋绑定状态 */
    private String roomStatus;
}
