package com.fqq.futurecommunity.community.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fqq.futurecommunity.common.utils.CommunityConvert;
import com.fqq.futurecommunity.community.domain.Community;
import com.fqq.futurecommunity.community.domain.dto.CommunityDto;
import com.fqq.futurecommunity.community.domain.vo.CommunityOverviewVo;
import com.fqq.futurecommunity.community.domain.vo.CommunityVo;
import com.fqq.futurecommunity.community.mapper.CommunityMapper;
import com.fqq.futurecommunity.community.service.CommunityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fqq.futurecommunity.community.service.ICommunityScopeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 小区  服务实现类
 * </p>
 *
 * @author fqq
 * @since 2026年03月06日
 */
@Service
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper, Community> implements CommunityService {
    @Resource
    private CommunityMapper communityMapper;

    @Resource
    private ICommunityScopeService communityScopeService;

    private static final String CODE_PREFIX = "COMMUNITY_";
    @Override
    public Page<CommunityDto> selectCommunityList(Page<CommunityDto> page,
                                                  Community community) {
        Community scopedCommunity = scopeCommunityQuery(community);
        return communityMapper.queryList(page, scopedCommunity);
    }

    @Override
    public int insertCommunity(Community community) {
        //设置小区编码
        community.setCommunityCode(CODE_PREFIX + System.currentTimeMillis());
        return communityMapper.insert(community);
    }

    @Override
    public Community selectCommunityById(Long communityId) {
        communityScopeService.checkCommunityPermission(communityId);
        return communityMapper.selectById(communityId);
    }

    @Override
    public int updateCommunity(Community community) {
        if (community != null) {
            communityScopeService.checkCommunityPermission(community.getCommunityId());
        }
        return communityMapper.updateById(community);
    }

    @Override
    public int deleteCommunityByIds(Long[] communityIds) {
        for (Long communityId : communityIds) {
            communityScopeService.checkCommunityPermission(communityId);
        }
        return communityMapper.deleteBatchIds(Arrays.asList(communityIds));
    }

    //导出excel
    @Override
    public List<CommunityDto> selectCommunityListNoPage(Community community) {
        Community scopedCommunity = scopeCommunityQuery(community);
        return communityMapper.queryListNoPage(scopedCommunity);
    }

    //下拉列表
    @Resource
    private CommunityConvert communityConvert;

    @Override
    public List<CommunityVo> queryPullDown(Community community) {
        Community scopedCommunity = scopeCommunityQuery(community);
        List<CommunityDto> dtoList = communityMapper.queryListNoPage(scopedCommunity);

        // 一行搞定对象转换
        List<CommunityVo> voList = communityConvert.toVoList(dtoList);

        return voList;
    }

    /**
     * 查询单个小区地图卡片概览。
     */
    @Override
    public CommunityOverviewVo getOverview(Long communityId) {
        communityScopeService.checkCommunityPermission(communityId);
        CommunityOverviewVo vo = communityMapper.selectCommunityOverviewBase(communityId);
        if (vo == null) {
            return null;
        }

        Integer buildingCount = defaultZero(communityMapper.countBuildingByCommunityId(communityId));
        Integer roomCount = defaultZero(communityMapper.countRoomByCommunityId(communityId));
        Integer occupiedRoomCount = defaultZero(communityMapper.countOccupiedRoomByCommunityId(communityId));
        Integer parkingCount = defaultZero(communityMapper.countParkingByCommunityId(communityId));

        Integer deviceTotal = defaultZero(communityMapper.countDeviceByCommunityId(communityId));
        Integer deviceError = defaultZero(communityMapper.countErrorDeviceByCommunityId(communityId));

        Integer repairCount = defaultZero(communityMapper.countRepairByCommunityId(communityId));
        Integer repairPending = defaultZero(communityMapper.countPendingRepairByCommunityId(communityId));

        Integer complaintCount = defaultZero(communityMapper.countTodayComplaintByCommunityId(communityId));
        Integer visitorCount = defaultZero(communityMapper.countTodayVisitorByCommunityId(communityId));

        Integer validFeeBillCount = defaultZero(communityMapper.countValidFeeBillByCommunityId(communityId));
        Integer paidFeeBillCount = defaultZero(communityMapper.countPaidFeeBillByCommunityId(communityId));

        vo.setBuildingCount(buildingCount);
        vo.setRoomCount(roomCount);
        vo.setParkingCount(parkingCount);
        vo.setDeviceTotal(deviceTotal);
        vo.setDeviceError(deviceError);
        vo.setRepairCount(repairCount);
        vo.setRepairPending(repairPending);
        vo.setComplaintCount(complaintCount);
        vo.setVisitorCount(visitorCount);

        vo.setOccupancyRate(calcRate(occupiedRoomCount, roomCount));
        vo.setPaymentRate(validFeeBillCount == 0 ? null : calcRate(paidFeeBillCount, validFeeBillCount));

        fillStatus(vo);

        return vo;
    }

    /**
     * 查询 community 表中所有小区的地图卡片概览。
     *
     * currentCommunityId 只用于让当前点进来的小区排在第一位，便于前端默认展示右侧卡片；
     * 不再按 5 公里范围过滤。
     */
    @Override
    public List<CommunityOverviewVo> listAllOverview(Long currentCommunityId) {
        communityScopeService.checkCommunityPermission(currentCommunityId);
        List<Long> allowedCommunityIds = null;
        if (!communityScopeService.isAdmin()) {
            allowedCommunityIds = communityScopeService.getAllowedCommunityIds();
            if (allowedCommunityIds == null || allowedCommunityIds.isEmpty()) {
                return Collections.emptyList();
            }
        }

        List<Long> communityIds = communityMapper.selectAllOverviewCommunityIds(currentCommunityId, allowedCommunityIds);
        List<CommunityOverviewVo> result = new ArrayList<>();

        for (Long communityId : communityIds) {
            CommunityOverviewVo overview = getOverview(communityId);
            if (overview != null) {
                result.add(overview);
            }
        }

        return result;
    }

    /**
     * 兼容 nearby 接口：当前首页地图需要展示所有已录入小区。
     */
    @Override
    public List<CommunityOverviewVo> listNearbyOverview(Long currentCommunityId, Double radiusKm) {
        return listAllOverview(currentCommunityId);
    }

    private Community scopeCommunityQuery(Community community) {
        Community scopedCommunity = community == null ? new Community() : community;
        communityScopeService.putCommunityScope(scopedCommunity);
        return scopedCommunity;
    }

    private Integer defaultZero(Integer value) {
        return value == null ? 0 : value;
    }

    private BigDecimal calcRate(Integer numerator, Integer denominator) {
        if (denominator == null || denominator == 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(defaultZero(numerator))
                .divide(BigDecimal.valueOf(denominator), 4, RoundingMode.HALF_UP);
    }

    private void fillStatus(CommunityOverviewVo vo) {
        vo.setDeviceStatus(calcDeviceStatus(vo.getDeviceError()));
        vo.setRepairStatus(calcRepairStatus(vo.getRepairPending()));
        vo.setComplaintStatus(calcComplaintStatus(vo.getComplaintCount()));
        vo.setPaymentStatus(calcPaymentStatus(vo.getPaymentRate()));

        String overallStatus = calcOverallStatus(vo);
        vo.setOverallStatus(overallStatus);

        if ("danger".equals(overallStatus)) {
            vo.setStatusText("异常");
        } else if ("warning".equals(overallStatus)) {
            vo.setStatusText("预警");
        } else {
            vo.setStatusText("正常");
        }
    }

    private String calcDeviceStatus(Integer deviceError) {
        if (deviceError == null || deviceError == 0) {
            return "normal";
        }
        if (deviceError >= 5) {
            return "danger";
        }
        return "warning";
    }

    private String calcRepairStatus(Integer repairPending) {
        if (repairPending == null || repairPending == 0) {
            return "normal";
        }
        if (repairPending >= 10) {
            return "danger";
        }
        return "warning";
    }

    private String calcComplaintStatus(Integer complaintCount) {
        if (complaintCount == null || complaintCount == 0) {
            return "normal";
        }
        if (complaintCount >= 5) {
            return "danger";
        }
        return "warning";
    }

    private String calcPaymentStatus(BigDecimal paymentRate) {
        if (paymentRate == null) {
            return "normal";
        }
        if (paymentRate.compareTo(new BigDecimal("0.60")) < 0) {
            return "danger";
        }
        if (paymentRate.compareTo(new BigDecimal("0.80")) < 0) {
            return "warning";
        }
        return "normal";
    }

    private String calcOverallStatus(CommunityOverviewVo vo) {
        List<String> statuses = Arrays.asList(
                vo.getDeviceStatus(),
                vo.getRepairStatus(),
                vo.getComplaintStatus(),
                vo.getPaymentStatus()
        );

        if (statuses.contains("danger")) {
            return "danger";
        }
        if (statuses.contains("warning")) {
            return "warning";
        }
        return "normal";
    }

}
