package com.fqq.futurecommunity.common.core.domain;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.constant.HttpStatus;
import com.fqq.futurecommunity.common.core.page.PageDomain;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ServletUtils;

import java.util.List;

public class BaseController {

    public static final String PAGE_NUM = "pageNum";
    public static final String PAGE_SIZE = "pageSize";

    public static PageDomain getPageDomain() {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(ServletUtils.getParameterToInt(PAGE_NUM));
        pageDomain.setPageSize(ServletUtils.getParameterToInt(PAGE_SIZE));
        return pageDomain;
    }
    protected <T> Page<T> startPage() {
        PageDomain pageDomain = getPageDomain();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();

        if (pageNum == null || pageSize == null) {
            pageNum = 1;
            pageSize = 10;
        }

        return new Page<>(pageNum, pageSize);
    }

    protected <T> PageResult<T> getData(Page<T> page) {
        return PageResult.SUCCESS(page.getTotal(), page.getRecords());
    }

//    protected PageResult getData(Page<?> page) {
//        PageResult pageResult = new PageResult();
//        pageResult.setCode(HttpStatus.SUCCESS);
//        pageResult.setMsg("查询成功");
//        pageResult.setRows(page.getRecords());
//        pageResult.setTotal(page.getTotal());
//        return pageResult;
//    }

    protected PageResult getData(List<?> list) {
        PageResult pageResult = new PageResult();
        pageResult.setCode(HttpStatus.SUCCESS);
        pageResult.setMsg("查询成功");
        pageResult.setRows(list);
        pageResult.setTotal(list == null ? 0 : list.size());
        return pageResult;
    }

    protected BaseResponse<Void> toAjax(int rows) {
        return rows > 0 ? BaseResponse.success() : BaseResponse.fail("操作失败");
    }
}
