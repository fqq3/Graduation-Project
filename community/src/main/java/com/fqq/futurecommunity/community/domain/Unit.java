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

/** Unit */
@Getter
@Setter
@Accessors(chain = true)
@TableName("unit")
public class Unit extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "unit_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long unitId;

    @TableField("community_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;

    @TableField("building_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long buildingId;

    @TableField("unit_name")
    private String unitName;

    @TableField("unit_code")
    private String unitCode;

    @TableField("unit_level")
    private Integer unitLevel;

    @TableField("unit_acreage")
    private BigDecimal unitAcreage;

    @TableField("unit_have_elevator")
    private String unitHaveElevator;
}
