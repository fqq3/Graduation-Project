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
import java.time.LocalDateTime;

/** Building */
@Getter
@Setter
@Accessors(chain = true)
@TableName("building")
public class Building extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "building_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long buildingId;

    @TableField("building_name")
    private String buildingName;

    @TableField("building_code")
    private String buildingCode;

    @TableField("building_acreage")
    private BigDecimal buildingAcreage;

    @TableField("community_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;
}
