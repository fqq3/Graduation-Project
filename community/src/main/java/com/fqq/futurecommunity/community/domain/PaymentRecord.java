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

/** 缴费记录 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("community_payment_record")
public class PaymentRecord extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "payment_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long paymentId;

    @TableField("payment_no")
    private String paymentNo;
@JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("bill_id")
    private Long billId;
@JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("owner_id")
    private Long ownerId;
@TableField("owner_name")
    private String ownerName;
@JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("room_id")
    private Long roomId;
@TableField("payment_amount")
    private BigDecimal paymentAmount;
@TableField("payment_method")
    private String paymentMethod;
@TableField("payment_status")
    private String paymentStatus;
@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("payment_time")
    private LocalDateTime paymentTime;
@TableField("operator_name")
    private String operatorName;
@TableField("receipt_no")
    private String receiptNo;

    @TableField(exist = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;

    @TableField(exist = false)
    private String billNo;
}
