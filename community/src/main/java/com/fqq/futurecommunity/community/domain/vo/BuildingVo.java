package com.fqq.futurecommunity.community.domain.vo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.List;
@Data
public class BuildingVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long buildingId;

    private String buildingName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long code;

    private String name;

    private List<UnitVo> children;
}
