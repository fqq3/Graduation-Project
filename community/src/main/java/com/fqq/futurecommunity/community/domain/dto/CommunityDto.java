package com.fqq.futurecommunity.community.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * @author: fqq
 * @Date: 2026/3/9 - 03 - 09 - 0:38
 * @Description: com.fqq.futurecommunity.community.domain.dto
 * @version: 1.0
 */
@Setter
@Getter
public class FutureCommunityDto extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 2L;

    /**
     * 小区id
     */
    @JsonFormat(shape=JsonFormat.Shape.STRING)//把这个字段在返回 JSON 时强制转换成字符串类型
    private Long communityId;

    /**
     * 小区名称
     */
    @TableField("community_name")
    private String communityName;

    /**
     * 小区编码
     */
    @TableField("community_code")
    private String communityCode;

    /**
     * 省区划码
     */
    @TableField("community_province_code")
    private String communityProvinceCode;
    //省名称
    private String communityProvinceName;

    /**
     * 市区划码
     */
    private String communityCityCode;
    //市名称
    private String communityCityName;

    /**
     * 区县区划码
     */
    private String communityTownCode;
    //区名称
    private String communityTownName;

    /**
     * 详细地址
     */
    private String communityDetailedAddress;

    /**
     * 经度
     */
    private String communityLongitude;

    /**
     * 纬度
     */
    private String communityLatitude;

    /**
     * 物业id
     */
    private Long deptId;

    /**
     * 排序
     */
    private Integer communitySort;

}
