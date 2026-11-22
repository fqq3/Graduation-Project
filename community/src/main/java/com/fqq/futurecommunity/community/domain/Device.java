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
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 设备信息 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("community_device")
public class Device extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "device_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long deviceId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("community_id")
    private Long communityId;
@JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("building_id")
    private Long buildingId;
@JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("unit_id")
    private Long unitId;
@TableField("device_name")
    private String deviceName;
@TableField("device_code")
    private String deviceCode;
@TableField("device_type")
    private String deviceType;
@TableField("device_location")
    private String deviceLocation;
@TableField("device_status")
    private String deviceStatus;
@TableField("online_status")
    private String onlineStatus;
@TableField("manufacturer")
    private String manufacturer;
@JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("install_date")
    private LocalDate installDate;
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("last_maintain_time")
    private LocalDateTime lastMaintainTime;
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("next_maintain_time")
    private LocalDateTime nextMaintainTime;
@TableField("alarm_status")
    private String alarmStatus;
@TableField("alarm_content")
    private String alarmContent;
}
