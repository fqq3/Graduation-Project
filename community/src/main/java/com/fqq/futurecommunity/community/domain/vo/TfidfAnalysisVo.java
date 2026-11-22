package com.fqq.futurecommunity.community.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.ArrayList;
import java.util.List;

/** 投诉建议 TF-IDF 分析结果。 */
public class TfidfAnalysisVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long communityId;

    private String type;

    private String beginTime;

    private String endTime;

    private Integer totalDocumentCount;

    private List<TfidfKeywordVo> keywords = new ArrayList<>();

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getTotalDocumentCount() {
        return totalDocumentCount;
    }

    public void setTotalDocumentCount(Integer totalDocumentCount) {
        this.totalDocumentCount = totalDocumentCount;
    }

    public List<TfidfKeywordVo> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<TfidfKeywordVo> keywords) {
        this.keywords = keywords;
    }
}
