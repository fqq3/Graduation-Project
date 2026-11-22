package com.fqq.futurecommunity.system.domain;

/**
 * @author: fqq
 * @Date: 2026/4/18 - 04 - 18 - 19:17
 * @Description: com.fqq.futurecommunity.system.domain
 * @version: 1.0
 */

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import jakarta.validation.constraints.NotBlank;

/**
 * 字典数据表 对应实体
 */
public class SysDictData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 字典编码 */
    @TableId
    @ExcelProperty("字典编码")
    private Long dictCode;

    /** 字典排序 */
    @ExcelProperty("字典排序")
    private Long dictSort;

    /** 字典标签 */
    @ExcelProperty("字典标签")
    private String dictLabel;

    /** 字典键值 */
    @ExcelProperty("字典键值")
    private String dictValue;

    /** 字典类型 */
    @ExcelProperty("字典类型")
    private String dictType;

    /** 样式属性 */
    private String cssClass;

    /** 表格字典样式 */
    private String listClass;

    /** 是否默认（Y是 N否） */
    @ExcelProperty("是否默认") //, replace = {"是_Y", "否_N"}
    private String isDefault;

    /** 状态（0正常 1停用） */
    @ExcelProperty("状态") //, replace = {"正常_0", "停用_1"}
    private String status;

    public Long getDictCode() {
        return dictCode;
    }

    public void setDictCode(Long dictCode) {
        this.dictCode = dictCode;
    }

    public Long getDictSort() {
        return dictSort;
    }

    public void setDictSort(Long dictSort) {
        this.dictSort = dictSort;
    }

    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    public String getDictLabel() {
        return dictLabel;
    }

    public void setDictLabel(String dictLabel) {
        this.dictLabel = dictLabel;
    }

    @NotBlank(message = "字典键值不能为空")
    @Size(max = 100, message = "字典键值长度不能超过100个字符")
    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }

    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    public String getDictType() {
        return dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }

    @Size(max = 100, message = "样式属性长度不能超过100个字符")
    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getListClass() {
        return listClass;
    }

    public void setListClass(String listClass) {
        this.listClass = listClass;
    }

    /**
     * 是否默认（建议用于业务判断）
     */
    public boolean isDefault() {
        return "Y".equals(this.isDefault);
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("dictCode", getDictCode())
                .append("dictSort", getDictSort())
                .append("dictLabel", getDictLabel())
                .append("dictValue", getDictValue())
                .append("dictType", getDictType())
                .append("cssClass", getCssClass())
                .append("listClass", getListClass())
                .append("isDefault", getIsDefault())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}

