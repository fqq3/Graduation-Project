package com.fqq.futurecommunity.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fqq.futurecommunity.system.domain.SysArea;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: fqq
 * @Date: 2026/3/10 - 03 - 10 - 23:48
 * @Description: com.fqq.futurecommunity.system.mapper
 * @version: 1.0
 */
@Mapper
public interface SysAreaMapper extends BaseMapper<SysArea> {
    List<SysArea> findAll();
}
