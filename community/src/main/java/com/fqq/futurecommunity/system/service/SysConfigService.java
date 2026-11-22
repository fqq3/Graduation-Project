package com.fqq.futurecommunity.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.system.domain.SysConfig;

import java.util.List;

public interface SysConfigService {

    Page<SysConfig> selectConfigPage(Page<SysConfig> page, SysConfig config);

    List<SysConfig> selectConfigList(SysConfig config);

    SysConfig selectConfigById(Long configId);

    String selectConfigByKey(String configKey);

    int insertConfig(SysConfig config);

    int updateConfig(SysConfig config);

    int deleteConfigByIds(Long[] configIds);

    String checkConfigKeyUnique(SysConfig config);

    void clearCache();
}
