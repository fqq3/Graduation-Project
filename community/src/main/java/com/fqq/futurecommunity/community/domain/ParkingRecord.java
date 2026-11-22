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
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 停车记录 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("community_parking_record")
public class ParkingRecord extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "record_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long recordId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("vehicle_id")
    private Long vehicleId;
@JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("space_id")
    private Long spaceId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("owner_id")
    private Long ownerId;
    @TableField("community_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;
	@TableField("plate_no")
    private String plateNo;
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("in_time")
    private LocalDateTime inTime;
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("out_time")
    private LocalDateTime outTime;
@TableField("parking_duration")
    private Integer parkingDuration;
@TableField("parking_fee")
    private BigDecimal parkingFee;
@TableField("pay_status")
    private String payStatus;
@TableField("record_type")
    private String recordType;
}
