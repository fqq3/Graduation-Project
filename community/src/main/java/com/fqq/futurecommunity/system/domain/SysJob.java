package com.fqq.futurecommunity.system.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("sys_job")
public class SysJob extends BaseEntity {

    @TableId(value = "job_id", type = IdType.AUTO)
    @ExcelProperty("任务编号")
    private Long jobId;

    @ExcelProperty("任务名称")
    private String jobName;

    @ExcelProperty("任务组名")
    private String jobGroup;

    @ExcelProperty("调用目标字符串")
    private String invokeTarget;

    @ExcelProperty("cron表达式")
    private String cronExpression;

    @ExcelProperty("计划执行错误策略")
    private String misfirePolicy;

    @ExcelProperty("是否并发执行")
    private String concurrent;

    @ExcelProperty("状态")
    private String status;

    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date nextValidTime;
}
