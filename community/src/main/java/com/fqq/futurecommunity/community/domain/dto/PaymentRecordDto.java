package com.fqq.futurecommunity.community.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 缴费记录列表 DTO */
@Getter
@Setter
public class PaymentRecordDto extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long paymentId;
    private String paymentNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long billId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long ownerId;
    private String ownerName;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roomId;
    private BigDecimal paymentAmount;
    private String paymentMethod;
    private String paymentStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;
    private String operatorName;
    private String receiptNo;
    private String billNo;
    private String itemName;
    private String communityName;
    private String buildingName;
    private String unitName;
    private String roomName;
    private String ownerRealName;
    private String ownerPhoneNumber;
}
