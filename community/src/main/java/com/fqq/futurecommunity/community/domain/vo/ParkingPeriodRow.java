package com.fqq.futurecommunity.community.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/** Parking record time range. */
@Data
public class ParkingPeriodRow {
    private LocalDateTime inTime;

    private LocalDateTime outTime;

    private String recordType;

    private Long vehicleId;
}
