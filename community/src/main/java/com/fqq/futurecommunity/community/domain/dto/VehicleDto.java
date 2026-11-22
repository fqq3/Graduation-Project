package com.fqq.futurecommunity.community.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;


/** 车辆信息列表 DTO */
@Getter
@Setter
public class VehicleDto extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long vehicleId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long ownerId;
    private String ownerName;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roomId;
    private String plateNo;
    private String vehicleType;
    private String vehicleBrand;
    private String vehicleColor;
    private String phone;
    private String bindStatus;
    private String status;
    private String communityName;
    private String buildingName;
    private String unitName;
    private String roomName;
    private String ownerRealName;
    private String ownerPhoneNumber;
}
