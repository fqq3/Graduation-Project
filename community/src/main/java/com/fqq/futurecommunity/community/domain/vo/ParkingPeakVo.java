package com.fqq.futurecommunity.community.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Parking peak analysis result. */
@Data
public class ParkingPeakVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;

    private String date;

    private Long parkingCount;

    private String peakHour;

    private Integer peakCount;

    private BigDecimal pressureRate;

    private String pressureLevel;

    private String pressureText;

    private Integer fixedCarCount;

    private Integer tempCarCount;

    private BigDecimal fixedCarRate;

    private BigDecimal tempCarRate;

    private List<ParkingHourlyVo> hourlyData = new ArrayList<>();

    private List<ParkingTrendVo> last7Days = new ArrayList<>();
}
