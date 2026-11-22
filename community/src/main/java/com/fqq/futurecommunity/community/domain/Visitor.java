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

/** Visitor */
@Getter
@Setter
@Accessors(chain = true)
@TableName("visitor")
public class Visitor extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "visitor_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long visitorId;

    @TableField("community_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;

    @TableField("visitor_name")
    private String visitorName;

    @TableField("visitor_phone_number")
    private String visitorPhoneNumber;

    @TableField("visitor_date")
    private LocalDate visitorDate;

    @TableField("create_by_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long createById;

    @TableField("create_by_open_id")
    private String createByOpenId;
}
