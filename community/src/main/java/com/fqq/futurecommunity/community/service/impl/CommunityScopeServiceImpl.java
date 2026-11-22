package com.fqq.futurecommunity.community.service.impl;

import com.fqq.futurecommunity.common.constant.HttpStatus;
import com.fqq.futurecommunity.common.core.domain.BaseEntity;
import com.fqq.futurecommunity.common.core.exception.CustomException;
import com.fqq.futurecommunity.common.utils.SecurityUtils;
import com.fqq.futurecommunity.community.mapper.CommunityMapper;
import com.fqq.futurecommunity.community.service.ICommunityScopeService;
import com.fqq.futurecommunity.system.domain.LoginUser;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class CommunityScopeServiceImpl implements ICommunityScopeService {

    @Resource
    private CommunityMapper communityMapper;

    @Override
    public boolean isAdmin() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        return loginUser != null
                && loginUser.getUser() != null
                && loginUser.getUser().isAdmin();
    }

    @Override
    public List<Long> getAllowedCommunityIds() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return Collections.emptyList();
        }
        if (loginUser.getUser().isAdmin()) {
            return null;
        }
        Long deptId = loginUser.getUser().getDeptId();
        if (deptId == null) {
            return Collections.emptyList();
        }
        return communityMapper.selectCommunityIdsByDeptId(deptId);
    }

    @Override
    public boolean hasCommunityPermission(Long communityId) {
        if (communityId == null) {
            return false;
        }
        if (isAdmin()) {
            return true;
        }
        List<Long> allowedCommunityIds = getAllowedCommunityIds();
        return allowedCommunityIds != null
                && allowedCommunityIds.stream().anyMatch(id -> Objects.equals(id, communityId));
    }

    @Override
    public void checkCommunityPermission(Long communityId) {
        if (!hasCommunityPermission(communityId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "No permission for this community data");
        }
    }

    @Override
    public void putCommunityScope(BaseEntity entity) {
        if (entity == null || isAdmin()) {
            return;
        }
        if (entity.getParams() == null) {
            entity.setParams(new HashMap<>());
        }
        List<Long> allowedCommunityIds = getAllowedCommunityIds();
        if (allowedCommunityIds == null || allowedCommunityIds.isEmpty()) {
            entity.getParams().put("forceNoData", true);
        } else {
            entity.getParams().put("allowedCommunityIds", allowedCommunityIds);
        }
    }
}
