package com.fqq.futurecommunity.common.core.page;

/**
 * @author: fqq
 * @Date: 2026/3/10 - 03 - 10 - 0:58
 * @Description: com.fqq.futurecommunity.common.core.page
 * @version: 1.0
 */

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 分页请求参数
 */
@Setter
@Getter
public class PageDomain {

    /**
     * 当前页
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

}