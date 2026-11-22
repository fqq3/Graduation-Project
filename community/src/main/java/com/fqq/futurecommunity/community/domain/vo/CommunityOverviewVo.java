package com.fqq.futurecommunity.community.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 小区地图运行概览 VO。
 *
 * 用于地图 Marker 和右侧运行状态信息卡片展示。
 */
@Getter
@Setter
public class CommunityOverviewVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;

    private String communityName;

    private String address;

    private String communityCode;

    private String longitude;

    private String latitude;

    private Integer buildingCount;

    private Integer roomCount;

    private Integer parkingCount;

    private Integer deviceTotal;

    private Integer deviceError;

    /**
     * normal / warning / danger
     */
    private String deviceStatus;

    private Integer repairCount;

    private Integer repairPending;

    /**
     * normal / warning / danger
     */
    private String repairStatus;

    private Integer complaintCount;

    /**
     * normal / warning / danger
     */
    private String complaintStatus;

    private BigDecimal occupancyRate;

    private BigDecimal paymentRate;

    /**
     * normal / warning / danger
     */
    private String paymentStatus;

    private Integer visitorCount;

    /**
     * 正常 / 预警 / 异常
     */
    private String statusText;

    /**
     * normal / warning / danger
     *
     * 给地图 Marker 使用：
     * - normal：所有指标正常
     * - warning：至少一项预警且无异常
     * - danger：至少一项异常
     */
    private String overallStatus;
}
