package com.fqq.futurecommunity.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.constant.HttpStatus;
import com.fqq.futurecommunity.common.core.exception.CustomException;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.community.domain.Device;
import com.fqq.futurecommunity.community.domain.dto.DeviceDto;
import com.fqq.futurecommunity.community.mapper.DeviceMapper;
import com.fqq.futurecommunity.community.service.DeviceService;
import com.fqq.futurecommunity.community.service.ICommunityScopeService;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/** 设备信息 ServiceImpl */
@Service
public class DeviceServiceImpl implements DeviceService {
    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private ICommunityScopeService communityScopeService;

    @Override
    public Page<DeviceDto> selectDeviceList(Page<DeviceDto> page, Device q) {
        Device scopedDevice = scopeDeviceQuery(q);
        return deviceMapper.selectDeviceList(page, scopedDevice);
    }

    @Override
    public Device selectDeviceById(Long id) {
        Device device = deviceMapper.selectById(id);
        if (device != null) {
            communityScopeService.checkCommunityPermission(device.getCommunityId());
        }
        return device;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDevice(Device q) {
        communityScopeService.checkCommunityPermission(q.getCommunityId());
        if (q.getDeviceStatus() == null || q.getDeviceStatus().isBlank()) {
            q.setDeviceStatus("0");
        }
        if (q.getOnlineStatus() == null || q.getOnlineStatus().isBlank()) {
            q.setOnlineStatus("0");
        }
        if (q.getAlarmStatus() == null || q.getAlarmStatus().isBlank()) {
            q.setAlarmStatus("0");
        }
        fillCreate(q);
        int rows = deviceMapper.insert(q);

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDevice(Device q) {
        Device old = deviceMapper.selectById(q.getDeviceId());
        if (old == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Device data does not exist");
        }
        communityScopeService.checkCommunityPermission(old.getCommunityId());
        if (q.getCommunityId() != null) {
            communityScopeService.checkCommunityPermission(q.getCommunityId());
        }
        fillUpdate(q);
        return deviceMapper.updateById(q);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDeviceByIds(Long[] ids) {
        int rows = 0;
        for (Long id : ids) {
            Device old = deviceMapper.selectById(id);
            if (old != null) {
                communityScopeService.checkCommunityPermission(old.getCommunityId());
            }
            rows += deviceMapper.deleteById(id);
        }
        return rows;
    }

    private Device scopeDeviceQuery(Device q) {
        Device scopedDevice = q == null ? new Device() : q;
        communityScopeService.putCommunityScope(scopedDevice);
        return scopedDevice;
    }

    private void fillCreate(Device q) {
        Date now = new Date();
        q.setCreateTime(now);
        q.setUpdateTime(now);
        try {
            String username = SecurityUtils.getUserName();
            q.setCreateBy(username);
            q.setUpdateBy(username);
        } catch (Exception ignored) {
            q.setCreateBy("admin");
            q.setUpdateBy("admin");
        }
    }

    private void fillUpdate(Device q) {
        q.setUpdateTime(new Date());
        try {
            q.setUpdateBy(SecurityUtils.getUserName());
        } catch (Exception ignored) {
            q.setUpdateBy("admin");
        }
    }


}
