package com.fqq.futurecommunity.community.service;

import com.fqq.futurecommunity.common.core.domain.BaseEntity;

import java.util.List;

public interface ICommunityScopeService {

    boolean isAdmin();

    List<Long> getAllowedCommunityIds();

    boolean hasCommunityPermission(Long communityId);

    void checkCommunityPermission(Long communityId);

    void putCommunityScope(BaseEntity entity);
}
