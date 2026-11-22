package com.fqq.futurecommunity.system.domain;
import com.baomidou.mybatisplus.annotation.TableField;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author: fqq
 * @Date: 2026/4/11 - 04 - 11 - 18:55
 * @Description: com.fqq.futurecommunity.system.domain
 * @version: 1.0
 * 用户对象 sys_user
 */
public class SysUser extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @ExcelProperty("用户序号")
    @TableId
    private Long userId;

    /** 部门ID */
    @ExcelProperty("部门编号")
    private Long deptId;

    /** 用户账号 */
    @ExcelProperty("登录名称")
    private String userName;

    /** 用户昵称 */
    @ExcelProperty("用户名称")
    private String nickName;

    /** 用户邮箱 */
    @ExcelProperty("用户邮箱")
    private String email;

    /** 手机号码 */
    @ExcelProperty("手机号码")
    private String phonenumber;

    /** 用户性别（原始值：0男 1女 2未知） */
    private String sex;

    /** 用户性别（导出用） */
    @TableField(exist = false)
    @ExcelProperty("用户性别")
    private String sexText;

    /** 用户头像 */
    private String avatar;

    /** 密码 */
    private String password;

    /** 盐加密 */
    private String salt;

    /** 帐号状态（原始值：0正常 1停用） */
    private String status;

    /** 状态（导出用） */
    @ExcelProperty("帐号状态")
    @TableField(exist = false)
    private String statusText;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    /** 最后登录IP */
    @ExcelProperty("最后登录IP")
    private String loginIp;

    /** 最后登录时间 */
    @ExcelProperty("最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginDate;

    public SysUser() {
    }
    /** 部门对象 */
    @TableField(exist = false)
    private SysDept dept;
    /** 角色对象 */
    @TableField(exist = false)
    private List<SysRole> roles;
    /** 角色组 */
    @TableField(exist = false)
    private Long[] roleIds;
    @TableField(exist = false)
    /** 岗位组 */
    private Long[] postIds;
    //判断当前用户是否是admin
    public boolean isAdmin() {
        return isAdmin(this.userId);
    }
    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

    // ================== Getter / Setter ==================

    public SysDept getDept() {
        return dept;
    }

    public void setDept(SysDept dept) {
        this.dept = dept;
    }

    public List<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }

    public Long[] getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Long[] roleIds) {
        this.roleIds = roleIds;
    }

    public Long[] getPostIds() {
        return postIds;
    }

    public void setPostIds(Long[] postIds) {
        this.postIds = postIds;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    @NotBlank(message = "用户账号不能为空")
    @Size(max = 30, message = "用户账号长度不能超过30个字符")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Size(max = 11, message = "手机号码长度不能超过11个字符")
    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSexText() {
        return sexText;
    }

    public void setSexText(String sexText) {
        this.sexText = sexText;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    @Override
    public String toString() {
        return "SysUser{" +
                "userId=" + userId +
                ", deptId=" + deptId +
                ", userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", email='" + email + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", sex='" + sex + '\'' +
                ", sexText='" + sexText + '\'' +
                ", avatar='" + avatar + '\'' +
                ", status='" + status + '\'' +
                ", statusText='" + statusText + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", loginIp='" + loginIp + '\'' +
                ", loginDate=" + loginDate +
                ", dept=" + dept +
                ", roles=" + roles +
                ", roleIds=" + Arrays.toString(roleIds) +
                ", postIds=" + Arrays.toString(postIds) +
                '}';
    }
}
