package com.fqq.futurecommunity.community.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 停车记录列表 DTO */
@Getter
@Setter
public class ParkingRecordDto extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long recordId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long vehicleId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long spaceId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long ownerId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;
    private String plateNo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime outTime;
    private Integer parkingDuration;
    private BigDecimal parkingFee;
    private String payStatus;
    private String recordType;
    private String spaceNo;
    private String spaceArea;
    private String communityName;
    private String ownerName;
    private String ownerPhoneNumber;
}
