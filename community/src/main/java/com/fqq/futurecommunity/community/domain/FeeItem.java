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

/** 费用项目 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("community_fee_item")
public class FeeItem extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "item_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long itemId;

    @TableField("item_name")
    private String itemName;
@TableField("item_type")
    private String itemType;
@TableField("charge_standard")
    private BigDecimal chargeStandard;
@TableField("charge_unit")
    private String chargeUnit;
@TableField("charge_cycle")
    private String chargeCycle;
@TableField("status")
    private String status;
}
