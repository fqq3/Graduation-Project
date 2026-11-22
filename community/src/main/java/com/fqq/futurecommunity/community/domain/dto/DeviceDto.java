package com.fqq.futurecommunity.community.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 设备信息列表 DTO */
@Getter
@Setter
public class DeviceDto extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long deviceId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long buildingId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long unitId;
    private String deviceName;
    private String deviceCode;
    private String deviceType;
    private String deviceLocation;
    private String deviceStatus;
    private String onlineStatus;
    private String manufacturer;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate installDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMaintainTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextMaintainTime;
    private String alarmStatus;
    private String alarmContent;
    private String communityName;
    private String buildingName;
    private String unitName;
}
