package com.fqq.futurecommunity.community.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/** Hourly vehicle presence count. */
@Data
public class ParkingHourlyVo {
    private String hour;

    private Integer count;

    private BigDecimal pressureRate;

    private String pressureLevel;
}
