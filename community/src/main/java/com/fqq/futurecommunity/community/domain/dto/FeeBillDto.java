package com.fqq.futurecommunity.community.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;

/** 费用账单列表 DTO */
@Getter
@Setter
public class FeeBillDto extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long billId;
    private String billNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long ownerId;
    private String ownerName;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long roomId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long itemId;
    private String itemName;
    private Integer billYear;
    private Integer billMonth;
    private BigDecimal receivableAmount;
    private BigDecimal receivedAmount;
    private BigDecimal unpaidAmount;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private String billStatus;
    private String communityName;
    private String buildingName;
    private String unitName;
    private String roomName;
    private String ownerRealName;
    private String ownerPhoneNumber;
}
