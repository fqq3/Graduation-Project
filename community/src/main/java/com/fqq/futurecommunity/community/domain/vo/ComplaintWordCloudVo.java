package com.fqq.futurecommunity.community.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** Complaint and suggestion word cloud analysis result. */
@Data
public class ComplaintWordCloudVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;

    private String beginTime;

    private String endTime;

    private List<WordCloudItemVo> complaintWords = new ArrayList<>();

    private List<WordCloudItemVo> suggestWords = new ArrayList<>();
}
