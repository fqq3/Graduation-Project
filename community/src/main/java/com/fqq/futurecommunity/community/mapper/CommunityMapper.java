package com.fqq.futurecommunity.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.community.domain.Community;
import com.fqq.futurecommunity.community.domain.dto.CommunityDto;
import com.fqq.futurecommunity.community.domain.vo.CommunityOverviewVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommunityMapper extends BaseMapper<Community> {

    /**
     * 分页查询小区列表
     */
    Page<CommunityDto> queryList(Page<CommunityDto> page, @Param("community") Community community);

    /**
     * 不分页查询小区列表
     */
    List<CommunityDto> queryListNoPage(@Param("community") Community community);

    List<Long> selectCommunityIdsByDeptId(@Param("deptId") Long deptId);

    /**
     * 查询单个小区地图卡片基础信息
     */
    CommunityOverviewVo selectCommunityOverviewBase(@Param("communityId") Long communityId);

    /**
     * 查询所有有经纬度的小区地图卡片基础信息
     */
    List<CommunityOverviewVo> selectCommunityOverviewBasesWithLocation();

    /**
     * 查询所有用于地图展示的小区 ID，并把当前小区排在第一位
     */
    List<Long> selectAllOverviewCommunityIds(@Param("currentCommunityId") Long currentCommunityId,
                                             @Param("communityIds") List<Long> communityIds);

    Integer countBuildingByCommunityId(@Param("communityId") Long communityId);

    Integer countRoomByCommunityId(@Param("communityId") Long communityId);

    Integer countOccupiedRoomByCommunityId(@Param("communityId") Long communityId);

    Integer countParkingByCommunityId(@Param("communityId") Long communityId);

    Integer countDeviceByCommunityId(@Param("communityId") Long communityId);

    Integer countErrorDeviceByCommunityId(@Param("communityId") Long communityId);

    Integer countRepairByCommunityId(@Param("communityId") Long communityId);

    Integer countPendingRepairByCommunityId(@Param("communityId") Long communityId);

    Integer countTodayComplaintByCommunityId(@Param("communityId") Long communityId);

    Integer countTodayVisitorByCommunityId(@Param("communityId") Long communityId);

    Integer countValidFeeBillByCommunityId(@Param("communityId") Long communityId);

    Integer countPaidFeeBillByCommunityId(@Param("communityId") Long communityId);
}
