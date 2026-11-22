package com.fqq.futurecommunity.system.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("sys_config")
public class SysConfig extends BaseEntity {

    @TableId(value = "config_id", type = IdType.AUTO)
    @ExcelProperty("参数主键")
    private Long configId;

    @ExcelProperty("参数名称")
    private String configName;

    @ExcelProperty("参数键名")
    private String configKey;

    @ExcelProperty("参数键值")
    private String configValue;

    @ExcelProperty("系统内置")
    private String configType;
}
