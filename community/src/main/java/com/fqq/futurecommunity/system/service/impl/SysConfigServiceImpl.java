package com.fqq.futurecommunity.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.constant.Constants;
import com.fqq.futurecommunity.common.constant.UserConstants;
import com.fqq.futurecommunity.common.core.exception.CustomException;
import com.fqq.futurecommunity.common.utils.RedisCache;
import com.fqq.futurecommunity.system.domain.SysConfig;
import com.fqq.futurecommunity.system.mapper.SysConfigMapper;
import com.fqq.futurecommunity.system.service.SysConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class SysConfigServiceImpl implements SysConfigService {

    @Resource
    private SysConfigMapper configMapper;

    @Resource
    private RedisCache redisCache;

    @Override
    public Page<SysConfig> selectConfigPage(Page<SysConfig> page, SysConfig config) {
        return configMapper.selectConfigPage(page, config);
    }

    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        return configMapper.selectConfigList(config);
    }

    @Override
    public SysConfig selectConfigById(Long configId) {
        return configMapper.selectById(configId);
    }

    @Override
    public String selectConfigByKey(String configKey) {
        String cacheKey = getCacheKey(configKey);
        String configValue = redisCache.getCacheObject(cacheKey);
        if (configValue != null) {
            return configValue;
        }
        SysConfig config = configMapper.selectConfigByKey(configKey);
        if (config == null) {
            return "";
        }
        redisCache.setCacheObject(cacheKey, config.getConfigValue());
        return config.getConfigValue();
    }

    @Override
    public int insertConfig(SysConfig config) {
        int rows = configMapper.insert(config);
        if (rows > 0) {
            clearCache(config.getConfigKey());
        }
        return rows;
    }

    @Override
    @Transactional
    public int updateConfig(SysConfig config) {
        SysConfig oldConfig = configMapper.selectById(config.getConfigId());
        int rows = configMapper.updateById(config);
        if (rows > 0) {
            if (oldConfig != null) {
                clearCache(oldConfig.getConfigKey());
            }
            clearCache(config.getConfigKey());
        }
        return rows;
    }

    @Override
    @Transactional
    public int deleteConfigByIds(Long[] configIds) {
        int rows = 0;
        for (Long configId : configIds) {
            SysConfig config = configMapper.selectById(configId);
            if (config == null) {
                continue;
            }
            if (UserConstants.YES.equals(config.getConfigType())) {
                throw new CustomException(500, "Can not delete built-in config");
            }
            rows += configMapper.deleteById(configId);
            clearCache(config.getConfigKey());
        }
        return rows;
    }

    @Override
    public String checkConfigKeyUnique(SysConfig config) {
        Long configId = config.getConfigId() == null ? -1L : config.getConfigId();
        SysConfig info = configMapper.checkConfigKeyUnique(config.getConfigKey());
        if (info != null && !Objects.equals(info.getConfigId(), configId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public void clearCache() {
        Collection<String> keys = redisCache.keys(Constants.SYS_CONFIG_KEY + "*");
        if (keys != null && !keys.isEmpty()) {
            redisCache.deleteObject(keys);
        }
    }

    private String getCacheKey(String configKey) {
        return Constants.SYS_CONFIG_KEY + configKey;
    }

    private void clearCache(String configKey) {
        if (configKey != null && !configKey.isEmpty()) {
            redisCache.deleteObject(getCacheKey(configKey));
        }
    }
}
