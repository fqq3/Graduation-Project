package com.fqq.futurecommunity.community.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 报修工单列表 DTO */
@Getter
@Setter
public class RepairOrderDto extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long repairId;
    private String repairNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long ownerId;
    private String ownerName;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roomId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;
    private String repairTitle;
    private String repairType;
    private String repairLocation;
    private String repairContent;
    private String contactName;
    private String contactPhone;
    private String repairImages;
    private String auditStatus;
    private String auditOpinion;
    private String auditBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;
    private String workerName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime repairStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime repairFinishTime;
    private String repairResult;
    private BigDecimal repairCost;
    private Integer satisfaction;
    private String status;
    private String communityName;
    private String buildingName;
    private String unitName;
    private String roomName;
    private String ownerRealName;
    private String ownerPhoneNumber;
}
