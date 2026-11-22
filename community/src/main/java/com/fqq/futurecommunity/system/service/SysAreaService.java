package com.fqq.futurecommunity.system.service;

import com.fqq.futurecommunity.system.domain.dto.SysAreaDto;

import java.util.List;

/**
 * @author: fqq
 * @Date: 2026/3/10 - 03 - 10 - 23:52
 * @Description: com.fqq.futurecommunity.system.service
 * @version: 1.0
 */
public interface SysAreaService {
    /** * 获取区域数据的完整树 * @param * @return: java.util.List<com.fqq.futurecommunity.system.domain.dto.SysAreaDto> */
    List<SysAreaDto> findAreaAsTree();
}
