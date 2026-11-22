package com.fqq.futurecommunity.common.core.page;

/**
 * @author: fqq
 * @Date: 2026/3/10 - 03 - 10 - 0:58
 * @Description: com.fqq.futurecommunity.common.core.page
 * @version: 1.0
 */

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询统一响应封装类

 **/
@Setter
@Getter
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 3L;

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<T> rows;

    /** 消息状态码 */
    private int code;

    /** 消息内容 */
    private String msg;

    //构造器
    public PageResult() {
    }

    /**
     * 分页
     * @param total 列表数据
     * @param rows  总记录数
     * @return: null
     */
    public PageResult(long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }
    public static <T> PageResult SUCCESS(long total, List<T> rows) {
        PageResult result = new PageResult();
        result.setCode(200);
        result.setMsg("success");
        result.setTotal(total);
        result.setRows(rows);
        return result;
    }

}
