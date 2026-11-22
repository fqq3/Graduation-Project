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

/** 费用账单 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("community_fee_bill")
public class FeeBill extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "bill_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long billId;

    @TableField("bill_no")
    private String billNo;
@JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("owner_id")
    private Long ownerId;
@TableField("owner_name")
    private String ownerName;
@JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("room_id")
    private Long roomId;
@JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("item_id")
    private Long itemId;
@TableField("item_name")
    private String itemName;
@TableField("bill_year")
    private Integer billYear;
@TableField("bill_month")
    private Integer billMonth;
@TableField("receivable_amount")
    private BigDecimal receivableAmount;
@TableField("received_amount")
    private BigDecimal receivedAmount;
@TableField("unpaid_amount")
    private BigDecimal unpaidAmount;
@JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("due_date")
    private LocalDate dueDate;
@TableField("bill_status")
    private String billStatus;

    @TableField(exist = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;
}
