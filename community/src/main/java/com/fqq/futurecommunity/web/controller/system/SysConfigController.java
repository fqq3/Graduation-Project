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
import com.fqq.futurecommunity.system.domain.SysConfig;
import com.fqq.futurecommunity.system.service.SysConfigService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/system/config")
public class SysConfigController extends BaseController {

    @Resource
    private SysConfigService configService;

    @GetMapping("/list")
    @PreAuthorize("@pe.hasAnyPerms('system:config:list,system:config:query')")
    public PageResult<SysConfig> list(SysConfig config) {
        Page<SysConfig> page = startPage();
        return getData(configService.selectConfigPage(page, config));
    }

    @GetMapping("/{configId}")
    @PreAuthorize("@pe.hasAnyPerms('system:config:list,system:config:query')")
    public BaseResponse<SysConfig> getInfo(@PathVariable Long configId) {
        return BaseResponse.success(configService.selectConfigById(configId));
    }

    @GetMapping("/configKey/{configKey}")
    @PreAuthorize("@pe.hasAnyPerms('system:config:list,system:config:query')")
    public BaseResponse<String> getConfigKey(@PathVariable String configKey) {
        return BaseResponse.success(configService.selectConfigByKey(configKey));
    }

    @PostMapping
    @PreAuthorize("@pe.hasPerms('system:config:add')")
    public BaseResponse<Void> add(@RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return BaseResponse.fail("Config key already exists");
        }
        config.setCreateBy(SecurityUtils.getUserName());
        return toAjax(configService.insertConfig(config));
    }

    @PutMapping
    @PreAuthorize("@pe.hasPerms('system:config:edit')")
    public BaseResponse<Void> edit(@RequestBody SysConfig config) {
        if (UserConstants.NOT_UNIQUE.equals(configService.checkConfigKeyUnique(config))) {
            return BaseResponse.fail("Config key already exists");
        }
        config.setUpdateBy(SecurityUtils.getUserName());
        return toAjax(configService.updateConfig(config));
    }

    @DeleteMapping("/{configIds}")
    @PreAuthorize("@pe.hasPerms('system:config:remove')")
    public BaseResponse<Void> remove(@PathVariable Long[] configIds) {
        return toAjax(configService.deleteConfigByIds(configIds));
    }

    @DeleteMapping("/clearCache")
    @PreAuthorize("@pe.hasPerms('system:config:remove')")
    public BaseResponse<Void> clearCache() {
        configService.clearCache();
        return BaseResponse.success();
    }

    @GetMapping("/export")
    @PreAuthorize("@pe.hasPerms('system:config:export')")
    public ChainedMap export(SysConfig config) {
        List<SysConfig> configList = configService.selectConfigList(config);
        String fileName = FileStorageUtils.generateFileName("config", "xlsx");
        Path filePath = FileStorageUtils.getDownloadDir().resolve(fileName);
        ExcelUtils.writeExcelToFile(configList, SysConfig.class, filePath, "Config");
        return ChainedMap.create().set("code", 200).set("msg", fileName);
    }
}
