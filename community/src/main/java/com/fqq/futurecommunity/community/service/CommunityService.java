package com.fqq.futurecommunity.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.Community;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fqq.futurecommunity.community.domain.dto.CommunityDto;
import com.fqq.futurecommunity.community.domain.vo.CommunityOverviewVo;
import com.fqq.futurecommunity.community.domain.vo.CommunityVo;

import java.util.List;

/**
 * <p>
 * 小区  服务类
 * </p>
 *
 * @author fqq
 * @since 2026年03月06日
 */
public interface CommunityService extends IService<Community> {
    //分页查询小区
    Page<CommunityDto> selectCommunityList(Page<CommunityDto> page,
                                           Community community);
    //导出excel
    List<CommunityDto> selectCommunityListNoPage(Community community);

    //新增小区
    int insertCommunity(Community community);

    //查询小区全部信息
    Community selectCommunityById(Long communityId);

    //修改小区
    int updateCommunity(Community community);

    //删除小区
    int deleteCommunityByIds(Long[] communityIds);

    //下拉列表
    List<CommunityVo> queryPullDown(Community community);

    //地图卡片：单个小区运行概览
    CommunityOverviewVo getOverview(Long communityId);

    //地图卡片：所有已录入小区运行概览。currentCommunityId 只用于前端默认选中/排序，不做范围过滤。
    List<CommunityOverviewVo> listAllOverview(Long currentCommunityId);

    //地图卡片：兼容 nearby 接口，返回所有已录入小区运行概览
    List<CommunityOverviewVo> listNearbyOverview(Long currentCommunityId, Double radiusKm);

}
