package com.fqq.futurecommunity.web.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.constant.UserConstants;
import com.fqq.futurecommunity.common.core.domain.BaseController;
import com.fqq.futurecommunity.common.core.domain.BaseResponse;
import com.fqq.futurecommunity.common.core.page.PageResult;
import com.fqq.futurecommunity.common.utils.ChainedMap;
import com.fqq.futurecommunity.common.utils.ExcelUtils;
import com.fqq.futurecommunity.common.utils.FileStorageUtils;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.system.domain.LoginUser;
import com.fqq.futurecommunity.system.domain.SysRole;
import com.fqq.futurecommunity.system.domain.SysUser;
import com.fqq.futurecommunity.system.domain.dto.SysUserExportDto;
import com.fqq.futurecommunity.system.domain.dto.SysUserImportDto;
import com.fqq.futurecommunity.system.service.SysPostService;
import com.fqq.futurecommunity.system.service.SysRoleService;
import com.fqq.futurecommunity.system.service.SysUserService;
import com.fqq.futurecommunity.system.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysPostService postService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/list")
    public PageResult<SysUser> list(SysUser user,
                                    @RequestParam int pageNum,
                                    @RequestParam int pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        return getData(userService.selectUserPage(page, user));
    }

    @GetMapping("/export")
    public ChainedMap export(SysUser user) {
        List<SysUser> users = userService.selectUserList(user);
        List<SysUserExportDto> rows = buildUserExportRows(users);
        String fileName = FileStorageUtils.generateFileName("user_export", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(rows, SysUserExportDto.class, filePath, "users");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }

    @GetMapping("/importTemplate")
    public ChainedMap importTemplate() {
        String fileName = FileStorageUtils.generateFileName("user_import_template", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(Collections.emptyList(), SysUserImportDto.class, filePath, "users");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }

    @PostMapping("/importData")
    public ChainedMap importData(@RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "updateSupport", defaultValue = "0") Integer updateSupport) throws IOException {
        if (file == null || file.isEmpty()) {
            return ChainedMap.create().set("code", 500).set("msg", "Import file cannot be empty");
        }

        List<SysUserImportDto> rows = ExcelUtils.readExcel(file.getInputStream(), SysUserImportDto.class);
        if (rows == null || rows.isEmpty()) {
            return ChainedMap.create().set("code", 500).set("msg", "Import file has no data");
        }

        boolean allowUpdate = updateSupport != null && updateSupport > 0;
        int inserted = 0;
        int updated = 0;
        int skipped = 0;
        List<String> failures = new ArrayList<>();

        for (int index = 0; index < rows.size(); index++) {
            SysUserImportDto row = rows.get(index);
            try {
                ImportResult result = saveImportedUser(row, allowUpdate);
                switch (result) {
                    case INSERTED -> inserted++;
                    case UPDATED -> updated++;
                    case SKIPPED -> skipped++;
                }
            } catch (Exception e) {
                failures.add("Row " + (index + 2) + ": " + e.getMessage());
            }
        }

        return ChainedMap.create().set("code", 200).set("msg", buildImportSummary(inserted, updated, skipped, failures));
    }

    @DeleteMapping("/{userIds}")
    public BaseResponse remove(@PathVariable Long[] userIds) {
        return toAjax(userService.deleteUserByIds(userIds));
    }

    @PutMapping("/resetPwd")
    public BaseResponse restPwd(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUserName());
        return toAjax(userService.resetPwd(user));
    }

    @PutMapping("/changeStatus")
    public BaseResponse changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setUpdateBy(SecurityUtils.getUserName());
        return toAjax(userService.updateUserStatus(user));
    }

    @GetMapping(value = {"/", "/{userId}"})
    public ChainedMap getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        ChainedMap map = ChainedMap.create().set("code", 200).set("msg", "success");

        List<SysRole> roleList = roleService.selectRoleAll();
        if (SysUser.isAdmin(userId)) {
            map.put("roles", roleList);
        } else {
            List<SysRole> currentRoles = roleList.stream()
                    .filter(role -> !role.isAdmin())
                    .collect(Collectors.toList());
            map.put("roles", currentRoles);
        }
        map.put("posts", postService.selectPostAll());

        if (!Objects.isNull(userId)) {
            map.put("data", userService.selectUserById(userId));
            map.put("postIds", postService.selectPostListByUserId(userId));
            map.put("roleIds", roleService.selectRoleListByUserId(userId));
        }
        return map;
    }

    @GetMapping("/profile")
    public ChainedMap profile() {
        String userName = SecurityUtils.getUserName();
        return ChainedMap.create()
                .set("code", 200)
                .set("msg", "success")
                .set("data", userService.selectUserByUserName(userName))
                .set("roleGroup", userService.selectUserRoleGroup(userName))
                .set("postGroup", userService.selectUserPostGroup(userName));
    }

    @PutMapping("/profile")
    public BaseResponse updateProfile(@RequestBody SysUser user) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser profile = new SysUser();
        profile.setUserId(loginUser.getUser().getUserId());
        profile.setNickName(user.getNickName());
        profile.setEmail(user.getEmail());
        profile.setPhonenumber(user.getPhonenumber());
        profile.setSex(user.getSex());
        profile.setUpdateBy(loginUser.getUsername());

        if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(profile))) {
            return BaseResponse.fail("Phone number already exists");
        }
        if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(profile))) {
            return BaseResponse.fail("Email already exists");
        }

        int rows = userService.updateUserProfile(profile);
        if (rows > 0) {
            refreshLoginUser(userService.selectUserByUserName(loginUser.getUsername()));
        }
        return toAjax(rows);
    }

    @PutMapping("/profile/updatePwd")
    public BaseResponse updatePwd(@RequestParam String oldPassword,
                                  @RequestParam String newPassword) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            return BaseResponse.fail("Password params cannot be empty");
        }
        if (!passwordEncoder.matches(oldPassword, loginUser.getPassword())) {
            return BaseResponse.fail("Old password is incorrect");
        }
        if (passwordEncoder.matches(newPassword, loginUser.getPassword())) {
            return BaseResponse.fail("New password cannot match old password");
        }

        String password = SecurityUtils.encryptPassword(newPassword);
        int rows = userService.resetUserPwd(loginUser.getUsername(), password);
        if (rows > 0) {
            loginUser.getUser().setPassword(password);
            tokenService.setLoginUser(loginUser);
            return BaseResponse.success();
        }
        return BaseResponse.fail("Password update failed");
    }

    @PostMapping("/profile/avatar")
    public ChainedMap uploadAvatar(@RequestParam("avatarfile") MultipartFile avatarFile) throws IOException {
        if (avatarFile == null || avatarFile.isEmpty()) {
            return ChainedMap.create().set("code", 500).set("msg", "Upload file cannot be empty");
        }
        String contentType = avatarFile.getContentType();
        if (!StringUtils.hasText(contentType) || !contentType.startsWith("image/")) {
            return ChainedMap.create().set("code", 500).set("msg", "Only image files are supported");
        }

        String suffix = StringUtils.getFilenameExtension(avatarFile.getOriginalFilename());
        String fileName = UUID.randomUUID().toString().replace("-", "");
        if (StringUtils.hasText(suffix)) {
            fileName = fileName + "." + suffix.toLowerCase();
        }

        Path avatarDir = FileStorageUtils.getAvatarDir();
        Files.createDirectories(avatarDir);
        Files.copy(avatarFile.getInputStream(), avatarDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

        String avatarPath = FileStorageUtils.buildProfileUrl("avatar", fileName);

        if (!userService.updateUserAvatar(SecurityUtils.getUserName(), avatarPath)) {
            return ChainedMap.create().set("code", 500).set("msg", "Avatar update failed");
        }

        LoginUser loginUser = SecurityUtils.getLoginUser();
        loginUser.getUser().setAvatar(avatarPath);
        tokenService.setLoginUser(loginUser);

        return ChainedMap.create()
                .set("code", 200)
                .set("msg", "success")
                .set("imgUrl", avatarPath);
    }

    @PostMapping
    public BaseResponse add(@RequestBody SysUser user) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            return BaseResponse.fail("Username already exists");
        }
        if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return BaseResponse.fail("Phone number already exists");
        }
        if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return BaseResponse.fail("Email already exists");
        }
        user.setCreateBy(SecurityUtils.getUserName());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    @PutMapping
    public BaseResponse edit(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);

        if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return BaseResponse.fail("Phone number already exists");
        }
        if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return BaseResponse.fail("Email already exists");
        }

        user.setUpdateBy(SecurityUtils.getUserName());
        return toAjax(userService.updateUser(user));
    }

    private void refreshLoginUser(SysUser currentUser) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        loginUser.setUser(currentUser);
        tokenService.setLoginUser(loginUser);
    }

    private List<SysUserExportDto> buildUserExportRows(List<SysUser> users) {
        List<SysUserExportDto> rows = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (SysUser user : users) {
            SysUserExportDto row = new SysUserExportDto();
            row.setUserId(user.getUserId());
            row.setUserName(user.getUserName());
            row.setNickName(user.getNickName());
            row.setDeptName(user.getDept() == null ? "" : user.getDept().getDeptName());
            row.setPhonenumber(defaultString(user.getPhonenumber()));
            row.setEmail(defaultString(user.getEmail()));
            row.setSexLabel(toSexLabel(user.getSex()));
            row.setStatusLabel("0".equals(user.getStatus()) ? "Enabled" : "Disabled");
            row.setCreateTime(user.getCreateTime() == null ? "" : formatter.format(user.getCreateTime()));
            rows.add(row);
        }
        return rows;
    }

    private ImportResult saveImportedUser(SysUserImportDto row, boolean allowUpdate) {
        if (!StringUtils.hasText(row.getUserName())) {
            throw new IllegalArgumentException("Login name is required");
        }
        if (!StringUtils.hasText(row.getNickName())) {
            throw new IllegalArgumentException("Display name is required");
        }
        if (row.getDeptId() != null && row.getDeptId() <= 0) {
            throw new IllegalArgumentException("Department ID must be greater than 0");
        }

        SysUser existing = userService.selectUserByUserName(row.getUserName().trim());
        if (existing != null && !allowUpdate) {
            return ImportResult.SKIPPED;
        }

        SysUser user = new SysUser();
        user.setUserName(row.getUserName().trim());
        user.setNickName(row.getNickName().trim());
        user.setDeptId(row.getDeptId());
        user.setPhonenumber(trimToNull(row.getPhonenumber()));
        user.setEmail(trimToNull(row.getEmail()));
        user.setSex(normalizeFlag(row.getSex(), existing == null ? "2" : null));
        user.setStatus(normalizeFlag(row.getStatus(), existing == null ? "0" : null));
        user.setRemark(trimToNull(row.getRemark()));
        user.setRoleIds(parseLongArray(row.getRoleIds()));
        user.setPostIds(parseLongArray(row.getPostIds()));

        if (existing == null) {
            String password = StringUtils.hasText(row.getPassword()) ? row.getPassword().trim() : "123456";
            user.setPassword(SecurityUtils.encryptPassword(password));
            user.setCreateBy(SecurityUtils.getUserName());
            validateImportedUser(user, true);
            userService.insertUser(user);
            return ImportResult.INSERTED;
        }

        user.setUserId(existing.getUserId());
        if (user.getRoleIds() == null) {
            user.setRoleIds(toLongArray(roleService.selectRoleListByUserId(existing.getUserId())));
        }
        if (user.getPostIds() == null) {
            user.setPostIds(toLongArray(postService.selectPostListByUserId(existing.getUserId())));
        }
        if (StringUtils.hasText(row.getPassword())) {
            user.setPassword(SecurityUtils.encryptPassword(row.getPassword().trim()));
        }
        user.setUpdateBy(SecurityUtils.getUserName());
        validateImportedUser(user, false);
        userService.updateUser(user);
        return ImportResult.UPDATED;
    }

    private void validateImportedUser(SysUser user, boolean isInsert) {
        if (isInsert && UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (StringUtils.hasText(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            throw new IllegalArgumentException("Phone number already exists");
        }
        if (StringUtils.hasText(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    private Long[] parseLongArray(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(Long::valueOf)
                .toArray(Long[]::new);
    }

    private Long[] toLongArray(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.stream().map(Long::valueOf).toArray(Long[]::new);
    }

    private String buildImportSummary(int inserted, int updated, int skipped, List<String> failures) {
        StringBuilder summary = new StringBuilder();
        summary.append("Imported: ").append(inserted).append("<br/>");
        summary.append("Updated: ").append(updated).append("<br/>");
        summary.append("Skipped: ").append(skipped).append("<br/>");
        summary.append("Failed: ").append(failures.size());
        if (!failures.isEmpty()) {
            summary.append("<br/><br/>");
            failures.stream().limit(10).forEach(item -> summary.append(item).append("<br/>"));
            if (failures.size() > 10) {
                summary.append("More failures omitted...");
            }
        }
        return summary.toString();
    }

    private String normalizeFlag(String value, String defaultValue) {
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }
        return value.trim();
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private String toSexLabel(String sex) {
        if ("0".equals(sex)) {
            return "Male";
        }
        if ("1".equals(sex)) {
            return "Female";
        }
        return "Unknown";
    }

    private enum ImportResult {
        INSERTED,
        UPDATED,
        SKIPPED
    }
}
