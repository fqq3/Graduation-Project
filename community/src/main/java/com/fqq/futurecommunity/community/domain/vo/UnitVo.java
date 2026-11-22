package com.fqq.futurecommunity.community.domain.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
@Data
public class UnitVo { @JsonFormat(shape = JsonFormat.Shape.STRING) private Long unitId; private String unitName; @JsonFormat(shape = JsonFormat.Shape.STRING) private Long code; private String name; }
