package com.fqq.futurecommunity.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.system.domain.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    Page<SysConfig> selectConfigPage(Page<SysConfig> page, @Param("config") SysConfig config);

    List<SysConfig> selectConfigList(@Param("config") SysConfig config);

    SysConfig selectConfigByKey(@Param("configKey") String configKey);

    SysConfig checkConfigKeyUnique(@Param("configKey") String configKey);
}
