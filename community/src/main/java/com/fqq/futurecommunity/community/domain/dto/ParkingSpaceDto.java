package com.fqq.futurecommunity.community.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;

/** 停车位列表 DTO */
@Getter
@Setter
public class ParkingSpaceDto extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long spaceId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;
    private String spaceNo;
    private String spaceArea;
    private String spaceType;
    private String useStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long ownerId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long vehicleId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private BigDecimal monthlyFee;
    private String communityName;
    private String ownerName;
    private String ownerPhoneNumber;
    private String plateNo;
}
