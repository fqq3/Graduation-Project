package com.fqq.futurecommunity.system.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@TableName("sys_job_log")
public class SysJobLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "job_log_id", type = IdType.AUTO)
    @ExcelProperty("日志编号")
    private Long jobLogId;

    @ExcelProperty("任务名称")
    private String jobName;

    @ExcelProperty("任务组名")
    private String jobGroup;

    @ExcelProperty("调用目标字符串")
    private String invokeTarget;

    @ExcelProperty("日志信息")
    private String jobMessage;

    @ExcelProperty("执行状态")
    private String status;

    @ExcelProperty("异常信息")
    private String exceptionInfo;

    @ExcelProperty("执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(exist = false)
    private Map<String, Object> params;
}
