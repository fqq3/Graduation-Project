package com.fqq.futurecommunity.community.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 小区 
 * </p>
 *
 * @author fqq
 * @since 2026年03月06日
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("community")
public class Community extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    /**
     * 小区id
     */
    @TableId(value = "community_id", type = IdType.ASSIGN_ID)
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

    /**
     * 市区划码
     */
    @TableField("community_city_code")
    private String communityCityCode;

    /**
     * 区县区划码
     */
    @TableField("community_town_code")
    private String communityTownCode;

    /**
     * 详细地址
     */
    @TableField("community_detailed_address")
    private String communityDetailedAddress;

    /**
     * 经度
     */
    @TableField("community_longitude")
    private String communityLongitude;

    /**
     * 纬度
     */
    @TableField("community_latitude")
    private String communityLatitude;

    /**
     * 物业id
     */
    @TableField("dept_id")
    @JsonFormat(shape=JsonFormat.Shape.STRING)//把这个字段在返回 JSON 时强制转换成字符串类型
    private Long deptId;

    /**
     * 排序
     */
    @TableField("community_sort")
    @JsonFormat(shape=JsonFormat.Shape.STRING)//把这个字段在返回 JSON 时强制转换成字符串类型
    private Integer communitySort;



}
