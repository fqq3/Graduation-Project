package com.fqq.futurecommunity.community.domain.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** Last 7 days parking trend item. */
@Data
public class ParkingTrendVo {
    private String date;

    private Integer count;

    private List<ParkingHourlyVo> hourlyData = new ArrayList<>();
}
