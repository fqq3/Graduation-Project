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


/** 车辆信息 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("community_vehicle")
public class Vehicle extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "vehicle_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long vehicleId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("owner_id")
    private Long ownerId;
@TableField("owner_name")
    private String ownerName;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("room_id")
    private Long roomId;
    @TableField(exist = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;
	@TableField("plate_no")
    private String plateNo;
@TableField("vehicle_type")
    private String vehicleType;
@TableField("vehicle_brand")
    private String vehicleBrand;
@TableField("vehicle_color")
    private String vehicleColor;
@TableField("phone")
    private String phone;
@TableField("bind_status")
    private String bindStatus;
@TableField("status")
    private String status;
}
