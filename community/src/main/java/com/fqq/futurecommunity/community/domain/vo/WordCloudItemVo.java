package com.fqq.futurecommunity.community.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Word and frequency item for word cloud charts. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordCloudItemVo {
    private String name;
    private Integer value;
}
