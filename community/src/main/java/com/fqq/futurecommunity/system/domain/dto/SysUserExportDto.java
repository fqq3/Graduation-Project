package com.fqq.futurecommunity.system.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysUserExportDto {

    @ExcelProperty("User ID")
    private Long userId;

    @ExcelProperty("Login Name")
    private String userName;

    @ExcelProperty("Display Name")
    private String nickName;

    @ExcelProperty("Department")
    private String deptName;

    @ExcelProperty("Phone")
    private String phonenumber;

    @ExcelProperty("Email")
    private String email;

    @ExcelProperty("Gender")
    private String sexLabel;

    @ExcelProperty("Status")
    private String statusLabel;

    @ExcelProperty("Created At")
    private String createTime;
}
