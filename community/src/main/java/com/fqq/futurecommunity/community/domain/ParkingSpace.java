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
import java.time.LocalDate;

/** 停车位 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("community_parking_space")
public class ParkingSpace extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "space_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long spaceId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("community_id")
    private Long communityId;
@TableField("space_no")
    private String spaceNo;
@TableField("space_area")
    private String spaceArea;
@TableField("space_type")
    private String spaceType;
@TableField("use_status")
    private String useStatus;
@JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("owner_id")
    private Long ownerId;
@JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("vehicle_id")
    private Long vehicleId;
@JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("start_date")
    private LocalDate startDate;
@JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("end_date")
    private LocalDate endDate;
@TableField("monthly_fee")
    private BigDecimal monthlyFee;
}
