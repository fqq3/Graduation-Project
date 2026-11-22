package com.fqq.futurecommunity.community.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 小区运行综合评价 TOPSIS 排名 VO。
 */
@Getter
@Setter
public class CommunityRankingVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;

    private String communityName;

    private Integer rank;

    private BigDecimal scorePercent;

    private String level;

    private String levelName;

    private BigDecimal occupancyRate;

    private BigDecimal paymentRate;

    private BigDecimal deviceHealth;

    private BigDecimal repairHealth;

    private BigDecimal complaintHealth;

    private BigDecimal parkingHealth;

    private List<String> mainRisks;
}
