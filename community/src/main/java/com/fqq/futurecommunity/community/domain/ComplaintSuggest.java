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

/** ComplaintSuggest */
@Getter
@Setter
@Accessors(chain = true)
@TableName("complaint_suggest")
public class ComplaintSuggest extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @TableId(value = "complaint_suggest_id", type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long complaintSuggestId;

    @TableField("community_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;

    @TableField("complaint_suggest_type")
    private String complaintSuggestType;

    @TableField("complaint_suggest_content")
    private String complaintSuggestContent;

    @TableField("user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
}
