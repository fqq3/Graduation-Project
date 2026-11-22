package com.fqq.futurecommunity.system.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysUserImportDto {

    @ExcelProperty("Login Name")
    private String userName;

    @ExcelProperty("Display Name")
    private String nickName;

    @ExcelProperty("Department ID")
    private Long deptId;

    @ExcelProperty("Phone")
    private String phonenumber;

    @ExcelProperty("Email")
    private String email;

    @ExcelProperty("Gender(0/1/2)")
    private String sex;

    @ExcelProperty("Status(0/1)")
    private String status;

    @ExcelProperty("Password")
    private String password;

    @ExcelProperty("Post IDs(Comma Separated)")
    private String postIds;

    @ExcelProperty("Role IDs(Comma Separated)")
    private String roleIds;

    @ExcelProperty("Remark")
    private String remark;
}
