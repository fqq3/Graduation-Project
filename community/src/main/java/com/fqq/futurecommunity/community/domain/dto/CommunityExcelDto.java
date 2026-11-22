package com.fqq.futurecommunity.community.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: fqq
 * @Date: 2026/3/21 - 03 - 21 - 2:57
 * @Description: com.fqq.futurecommunity.community.domain.dto
 * @version: 1.0
 */
@Data
public class CommunityExcelDto implements Serializable {

    /** 小区id */
    @ExcelProperty("序号")
    private Long communityId;

    /** 小区名称 */
    @ExcelProperty("小区名称")
    private String communityName;

    /** 小区编码 */
    @ExcelProperty("小区编码")
    private String communityCode;

    /** 省 */
    @ExcelProperty("省")
    private String communityProvinceName;

    /** 市 */
    @ExcelProperty("市")
    private String communityCityName;

    /** 区 */
    @ExcelProperty("区/县")
    private String communityTownName;

    /** 创建时间 */
    @ExcelProperty("创建时间")
    @DateTimeFormat("yyyy年MM月dd日")
    private Date createTime;

    /** 备注 */
    @ExcelProperty("备注")
    private String remark;
}
