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

/** 报修工单 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("community_repair_order")
public class RepairOrder extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "repair_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long repairId;

    @TableField("repair_no")
    private String repairNo;
@JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("owner_id")
    private Long ownerId;
@TableField("owner_name")
    private String ownerName;
@JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("room_id")
    private Long roomId;
@JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("community_id")
    private Long communityId;
@TableField("repair_title")
    private String repairTitle;
@TableField("repair_type")
    private String repairType;
@TableField("repair_location")
    private String repairLocation;
@TableField("repair_content")
    private String repairContent;
@TableField("contact_name")
    private String contactName;
@TableField("contact_phone")
    private String contactPhone;
@TableField("repair_images")
    private String repairImages;
@TableField("audit_status")
    private String auditStatus;
@TableField("audit_opinion")
    private String auditOpinion;
@TableField("audit_by")
    private String auditBy;
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("audit_time")
    private LocalDateTime auditTime;
@TableField("worker_name")
    private String workerName;
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("repair_start_time")
    private LocalDateTime repairStartTime;
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("repair_finish_time")
    private LocalDateTime repairFinishTime;
@TableField("repair_result")
    private String repairResult;
@TableField("repair_cost")
    private BigDecimal repairCost;
@TableField("satisfaction")
    private Integer satisfaction;
@TableField("status")
    private String status;
}
