package com.fqq.futurecommunity.community.domain.vo;

import lombok.Data;

/** Raw text row for complaint/suggestion analysis. */
@Data
public class AnalysisTextRow {
    private String complaintSuggestType;

    private String content;
}
